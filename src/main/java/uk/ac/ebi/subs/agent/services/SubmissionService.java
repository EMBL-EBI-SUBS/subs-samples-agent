package uk.ac.ebi.subs.agent.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SubmissionService {

    private String apiUrl;

    public void submit(SubmissionEnvelope envelope) {
        // TODO
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
