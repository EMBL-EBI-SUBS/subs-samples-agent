package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.biosamples.models.Sample;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private RestTemplateBuilder templateBuilder;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    private String apiUrl;

    public List<uk.ac.ebi.subs.data.submittable.Sample> submit(SubmissionEnvelope envelope) {
        List<uk.ac.ebi.subs.data.submittable.Sample> sampleList = envelope.getSubmission().getSamples();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        List<Sample> submittedSamples = new ArrayList<>();
        for (uk.ac.ebi.subs.data.submittable.Sample usiSample : sampleList) {
            Sample bioSample = toBsdSample.convert(usiSample);

            Sample responseSample = null;
            try {
                responseSample = templateBuilder
                        .build()
                        .postForObject(apiUrl, bioSample, Sample.class, headers);
            } catch(HttpClientErrorException e) {
                logger.error("Sample with id [" + usiSample.getId() + "] submission failed. ", e);
            }

            if(responseSample != null) {
                logger.info("Got sample accession [" + responseSample.getAccession() + "] back.");
                submittedSamples.add(responseSample);
            }
        }

        List<uk.ac.ebi.subs.data.submittable.Sample> usiSamples = toUsiSample.convert(submittedSamples);
        return usiSamples;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
