package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.exceptions.SampleNotFoundException;
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
            try {
                Sample found = findSample(accession);
                foundSamples.add(found);
            } catch (SampleNotFoundException e) {
                e.printStackTrace();
            }
        });

        return foundSamples;
    }

    private Sample findSample(String accession) throws SampleNotFoundException {
        logger.debug("Searching for sample {}", accession);

        try {
            return toUsiSample.convert(client.fetch(accession));
        } catch (RuntimeException e) {
            throw new SampleNotFoundException(accession, e);
        }
    }

}