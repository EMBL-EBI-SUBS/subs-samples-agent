package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;

@Service
@ConfigurationProperties
public class FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    @Autowired
    BioSamplesClient client;

    @Autowired
    BsdSampleToUsiSample toUsiSample;

    public List<Sample> findSamples(List<String> accessions) {
        List<Sample> foundSamples = new ArrayList<>();

        accessions.forEach(accession -> {
            Sample found = findSample(accession);
            foundSamples.add(found);
        });

        return foundSamples;
    }

    private Sample findSample(String accession) {
        logger.debug("Searching for sample {}", accession);

        try {
            return toUsiSample.convert(client.fetchSample(accession).get());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Could not find sample [" + accession + "]", e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Could not find sample [" + accession + "]", e);
        }
    }

}