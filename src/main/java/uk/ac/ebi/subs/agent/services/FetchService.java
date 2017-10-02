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
import java.util.NoSuchElementException;
import java.util.Optional;

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
            Optional<Sample> sample = findSample(accession);
            if (sample.isPresent()) {
                foundSamples.add(sample.get());
            }
        });
        return foundSamples;
    }

    private Optional<Sample> findSample(String accession) {
        logger.debug("Searching for sample {}", accession);
        try {
            return Optional.of(toUsiSample.convert(client.fetchSample(accession).get()));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Could not find sample [" + accession + "]", e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Could not find sample [" + accession + "]", e);
        } catch (NoSuchElementException e) {
            logger.warn("Could not find sample with accession {}", accession);
            return Optional.empty();
        }
    }

}