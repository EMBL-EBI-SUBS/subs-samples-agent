package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.subs.agent.biosamples.Sample;
import uk.ac.ebi.subs.agent.exception.*;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SupportingSamplesService {
    private static final Logger logger = LoggerFactory.getLogger(SupportingSamplesService.class);

    @Autowired
    RestTemplateBuilder templateBuilder;

    private String apiUrl;

    public List<Sample> findSamples(SubmissionEnvelope envelope) throws SampleNotFoundException{
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        List<Sample> samples = new ArrayList<>();

        sampleRefs.forEach(sampleRef -> {
            Sample sample;
            try {
                sample = templateBuilder.build().getForObject(apiUrl + sampleRef.getAccession(), Sample.class);
                samples.add(sample);
            } catch (HttpClientErrorException clientErrorException) {
                logger.error("Sample with accession [" + sampleRef.getAccession() + "] could not be found!");
            }
        });

        return samples;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
