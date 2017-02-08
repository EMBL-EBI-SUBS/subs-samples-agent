package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateService.class);

    public void update(SubmissionEnvelope envelope) {
        //TODO
    }

}
