package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.data.submittable.Sample;

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
        Assert.notNull(sampleList);
        ArrayList<Sample> submittedSamples = new ArrayList<>();

        for (Sample usiSample : sampleList) {
            String usiId = usiSample.getId();

            Sample submitted = submit(toBsdSample.convert(usiSample));
            submitted.setId(usiId);
            submittedSamples.add(submitted);
        }
        return submittedSamples;
    }

    private Sample submit(uk.ac.ebi.biosamples.model.Sample bsdSample) {
        logger.debug("Submitting sample.");

        try {
            return toUsiSample.convert(client.persistSample(bsdSample));
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            logger.error(e.getMessage());
            throw e;
        }

    }
}
