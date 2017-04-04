package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    BioSamplesClient client;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    public List<Sample> submit(List<Sample> sampleList) {
        ArrayList<Sample> submittedSamples = new ArrayList<>();
        sampleList.forEach(usiSample -> {
            uk.ac.ebi.biosamples.model.Sample bsdSample = toBsdSample.convert(usiSample);
            Sample submitted = submit(bsdSample);
            submittedSamples.add(submitted);
        });
        return submittedSamples;
    }

    private Sample submit(uk.ac.ebi.biosamples.model.Sample bsdSample) {
        logger.info("Submitting sample.");

        Sample usiSample = null;
        try {
            usiSample = toUsiSample.convert(client.persist(bsdSample));
        } catch (HttpClientErrorException e) {
            logger.error("Submission failed with error:", e);
        } catch (ResourceAccessException e) {
            logger.error("Could not reach BioSamples", e);
        } finally {
            if(usiSample == null) {
                usiSample = new Sample();
                return usiSample;
            }
        }
        logger.info("Got accession [" + usiSample.getAccession() + "]");
        return usiSample;
    }
}
