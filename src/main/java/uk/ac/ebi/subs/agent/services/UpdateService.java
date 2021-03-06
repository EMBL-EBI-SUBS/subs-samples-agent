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

/**
 * This service is responsible to submit a list of {@link Sample} to the BioSamples archive.
 * The sample could be a new sample or the user could update an already existing sample in the database.
 */
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

    public List<Sample> update(List<Sample> sampleList) {
        Assert.notNull(sampleList);
        ArrayList<Sample> updatedSamples = new ArrayList<>();

        for (Sample usiSample : sampleList) {
            String usiId = usiSample.getId();

            Sample updated = update(toBsdSample.convert(usiSample));
            updated.setId(usiId);
            updatedSamples.add(updated);
        }
        return updatedSamples;
    }

    private Sample update(uk.ac.ebi.biosamples.model.Sample bsdSample) {
        logger.debug("Updating sample {}", bsdSample.getAccession());

        try {
            return toUsiSample.convert(client.persistSample(bsdSample));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Update [" + bsdSample.getAccession() + "] failed with error:", e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Update [" + bsdSample.getAccession() + "] failed with error:", e);
        }

    }
}