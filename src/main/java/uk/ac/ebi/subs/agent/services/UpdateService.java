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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.net.URI;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "biosamples")
public class UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    private String apiUrl;

    public void update(List<Sample> sampleList) {
        sampleList.forEach(usiSample -> {
            uk.ac.ebi.biosamples.model.Sample bsdSample = toBsdSample.convert(usiSample);
            if(update(bsdSample)) {
                usiSample.setStatus(ProcessingStatus.Done);
            } else {
                usiSample.setStatus(ProcessingStatus.ActionRequired);
            }
        });
    }

    private boolean update(uk.ac.ebi.biosamples.model.Sample bsdSample) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiUrl)
                .path(bsdSample.getAccession())
                .build()
                .toUri();
        logger.debug("URI: " + uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<uk.ac.ebi.biosamples.model.Sample> requestEntity;
        ResponseEntity<uk.ac.ebi.biosamples.model.Sample> responseEntity;

        try {
            requestEntity = new RequestEntity<>(bsdSample, headers, HttpMethod.PUT, uri);
            responseEntity = restTemplate.exchange(requestEntity, uk.ac.ebi.biosamples.model.Sample.class);
            if(!responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.error("Unable to PUT [" + bsdSample.getAccession() + "]:" + responseEntity.toString());
                return false;
            }
        } catch (HttpClientErrorException e) {
            logger.error("Update [" + bsdSample.getAccession() + "] failed with error:", e);
            return false;
        }
        return true;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}