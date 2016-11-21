package uk.ac.ebi.subs.agent.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.agent.biosamples.Sample;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SupportingSamplesService {

    @Autowired
    RestTemplateBuilder templateBuilder;

    private String apiUrl;

    public List<Sample> findSamples(SubmissionEnvelope envelope) {
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        List<Sample> samples = new ArrayList<>();

        sampleRefs.forEach(sampleRef -> {
            // FIXME - USI sample object doesn't map properly to BioSamples sample object
            Sample sample = templateBuilder.build().getForObject(apiUrl + sampleRef.getReferencedObject().getAccession(), Sample.class);
            samples.add(sample);
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
