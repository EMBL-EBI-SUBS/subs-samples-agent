package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.net.URI;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateService.class);

    @Autowired
    BioSamplesClient client;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    public void update(List<Sample> sampleList) {
        sampleList.forEach(usiSample -> {
            update(toBsdSample.convert(usiSample));
        });
    }

    private boolean update(uk.ac.ebi.biosamples.model.Sample bsdSample) {
        logger.info("Updating sample.");

        try {
            client.persist(bsdSample);
        } catch (HttpClientErrorException e) {
            logger.error("Update [" + bsdSample.getAccession() + "] failed with error:", e);
            return false;
        } catch (ResourceAccessException e) {
            logger.error("Could not reach BioSamples", e);
            return false;
        }
        return true;
    }
}