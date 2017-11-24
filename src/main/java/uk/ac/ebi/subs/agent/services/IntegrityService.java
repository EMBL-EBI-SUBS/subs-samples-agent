package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class IntegrityService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    @Autowired
    private BioSamplesClient client;

    public boolean doesSampleExistInBiosamples(Sample sample) {
        logger.debug("Searching for sample by team and alias.");
        return searchByTeamNameAndAlias(sample.getTeam().getName(), sample.getAlias()).isPresent();
    }

    public void fillInSampleAccessionIfTeamAndAliasExistInBioSamples(Sample s){
        Optional<uk.ac.ebi.biosamples.model.Sample> optionalBioSampleEntry = this.searchByTeamNameAndAlias(
                s.getTeam().getName(),
                s.getAlias()
        );

        if (optionalBioSampleEntry.isPresent()){
            s.setAccession( optionalBioSampleEntry.get().getAccession() );
        }
    }

    private Optional<uk.ac.ebi.biosamples.model.Sample> searchByTeamNameAndAlias(String teamName, String alias) {
        try {
            Iterator<Resource<uk.ac.ebi.biosamples.model.Sample>> iterator = client
                    .fetchSampleResourceAll("\"" + teamName + "\" AND \"" + alias + "\"")
                    .iterator();

            Stream<Resource<uk.ac.ebi.biosamples.model.Sample>> sampleResourceStream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),false
            );

            return sampleResourceStream
                    .map(Resource::getContent)
                    .filter(s -> s.getDomain().equals(teamName) && s.getName().equals(alias))
                    .findFirst();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Something went wrong", e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

}
