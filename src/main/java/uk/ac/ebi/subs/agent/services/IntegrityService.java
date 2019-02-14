package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.biosamples.model.filter.Filter;
import uk.ac.ebi.biosamples.service.FilterBuilder;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This service is responsible to fil in the sample's accession ID if the team and alias exists in the BioSamples archive.
 */
@Service
public class IntegrityService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private BioSamplesClient client;

    public IntegrityService(BioSamplesClient client) {
        this.client = client;
    }

    public boolean doesSampleExistInBioSamples(String accessionId) {
        logger.debug("Searching for sample by accession id.");
        return searchByAccessionId(accessionId).isPresent();
    }

    private Optional<uk.ac.ebi.biosamples.model.Sample> searchByAccessionId(String accessionId) {
        return client.fetchSampleResource(accessionId).map(Resource::getContent);
    }
}
