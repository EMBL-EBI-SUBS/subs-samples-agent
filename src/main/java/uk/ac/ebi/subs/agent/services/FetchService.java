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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.exceptions.SampleNotFoundException;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class   FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    BsdSampleToUsiSample toUsiSample;

    private String apiUrl;

    public List<Sample> findSamples(SubmissionEnvelope envelope) {
        Set<SampleRef> sampleRefs = envelope.getSupportingSamplesRequired();
        List<Sample> sampleList = new ArrayList<>();
        Set<String> sampleSet = identifySamplesToFind(sampleRefs);

        sampleSet.forEach(acc -> {
            Sample usiSample = null;
            try {
                usiSample = findSample(acc);
            } catch (SampleNotFoundException e) {
                logger.error("Sample not found.", e);
            }
            if(usiSample != null) {
                sampleList.add(usiSample);
                sampleRefs.removeIf(ref -> acc.equals(ref.getAccession()));
            }
        });
        return sampleList;
    }

    private Sample findSample(String accession) throws SampleNotFoundException {
        URI uri = UriComponentsBuilder
                .fromUriString(apiUrl)
                .path(accession)
                .build()
                .toUri();
        logger.debug("URI: " + uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<uk.ac.ebi.biosamples.model.Sample> requestEntity;
        ResponseEntity<uk.ac.ebi.biosamples.model.Sample> responseEntity;

        Sample usiSample = null;
        try {
            requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
            responseEntity = restTemplate.exchange(requestEntity, uk.ac.ebi.biosamples.model.Sample.class);
            usiSample = toUsiSample.convert(responseEntity.getBody());
        } catch (HttpServerErrorException e) {
            throw new SampleNotFoundException(accession, e);
        } catch (ResourceAccessException e) {
            logger.error("Could not reach BioSamples", e);
        }
        return usiSample;
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

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
