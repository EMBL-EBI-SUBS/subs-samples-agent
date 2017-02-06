package uk.ac.ebi.subs.agent.services;

import org.slf4j.*;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateService.class);

    public void update() {
        //TODO
    }

}
