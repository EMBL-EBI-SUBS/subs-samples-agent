package uk.ac.ebi.subs.agent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@ConfigurationProperties
public class   FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    @Autowired
    BioSamplesClient client;

    @Autowired
    BsdSampleToUsiSample toUsiSample;

    public List<Sample> findSamples(SubmissionEnvelope envelope) {
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        List<Sample> sampleList = new ArrayList<>();
        Set<String> sampleSet = identifySamplesToFind(sampleRefs);

        for (String acc : sampleSet) {
            Sample sample = null;
            try {
                sample = toUsiSample.convert(client.fetchResource(acc).getContent());
            } catch (RuntimeException e) {
                //throw new SampleNotFoundException(acc, e);
                logger.error("Could not find sample with accession [" + acc + "]", e);
            }
            if(sample !=  null) {
                sampleList.add(sample);
            }
        }
        return sampleList;
    }

    private Set<String> identifySamplesToFind(Set<SampleRef> sampleRefs) {
        Set<String> sampleAccessions = new TreeSet<>();
        sampleRefs.forEach(ref -> {
            if(ref.getAccession() != null && !ref.getAccession().isEmpty()) {
                sampleAccessions.add(ref.getAccession());
            }
        });
        return sampleAccessions;
    }

}