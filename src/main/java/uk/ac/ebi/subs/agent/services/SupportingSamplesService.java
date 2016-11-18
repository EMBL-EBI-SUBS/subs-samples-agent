package uk.ac.ebi.subs.agent.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SupportingSamplesService {

    @Autowired
    RestTemplate restTemplate;

    private String apiUrl;

    public void findSamples(SubmissionEnvelope envelope) {
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        List<Sample> samples = new ArrayList<>();

        sampleRefs.forEach(sampleRef -> {
            Sample sample = restTemplate.getForObject(apiUrl + sampleRef.getReferencedObject().getAccession(), Sample.class);
            samples.add(sample);
        });

        envelope.setSupportingSamples(samples);
        envelope.getSupportingSamplesRequired().clear();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
