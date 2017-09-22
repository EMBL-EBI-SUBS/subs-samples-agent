package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrityService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    @Autowired
    private BioSamplesClient client;

    public boolean doesSampleExistInBiosamples(Sample sample) {
        logger.debug("Searching for sample by team and alias.");
        return searchByTeamNameAndAlias(sample.getTeam().getName(), sample.getAlias());
    }

    private boolean searchByTeamNameAndAlias(String teamName, String alias) {
        List<uk.ac.ebi.biosamples.model.Sample> bsdSampleList = new ArrayList<>();

        try {
            client.fetchSampleResourceAll("\"" + teamName + "\" AND \"" + alias + "\"")
                    .iterator()
                    .forEachRemaining(sampleResource -> bsdSampleList.add(sampleResource.getContent()));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Something went wrong", e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Something went wrong", e);
        }

        return bsdSampleList.stream()
                .filter(s -> s.getDomain().equals(teamName) && s.getName().equals(alias)).findFirst()
                .isPresent();
    }

}
