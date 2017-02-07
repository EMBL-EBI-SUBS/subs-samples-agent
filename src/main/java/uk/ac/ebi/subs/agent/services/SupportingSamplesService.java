package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.biosamples.models.Sample;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.*;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SupportingSamplesService {
    private static final Logger logger = LoggerFactory.getLogger(SupportingSamplesService.class);

    @Autowired
    private RestTemplateBuilder templateBuilder;

    private String apiUrlGet;

    public List<Sample> findSamples(SubmissionEnvelope envelope) {
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        Set<String> sampleAccessions = new TreeSet<>();
        List<Sample> samples = new ArrayList<>();

        sampleRefs
                .stream()
                .filter(sampleRef -> sampleRef.getAccession() != null)
                .collect(Collectors.toSet())
                .forEach(sampleRef -> sampleAccessions.add(sampleRef.getAccession()));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Sample sample;
        for (String accession : sampleAccessions) {
            try {
                sample = templateBuilder
                        .build()
                        .getForObject(apiUrlGet + accession, Sample.class, headers);

                samples.add(sample);
            } catch (HttpClientErrorException e) {
                logger.error("Sample [" + accession + "] not found!");
            }
        }

        return samples;
    }

    public String getApiUrlGet() {
        return apiUrlGet;
    }

    public void setApiUrlGet(String apiUrlGet) {
        this.apiUrlGet = apiUrlGet;
    }
}
