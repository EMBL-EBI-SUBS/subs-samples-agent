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
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    private String apiUrl;

    public List<Sample> submit(List<Sample> sampleList) {
        sampleList.forEach(usiSample -> {
            uk.ac.ebi.biosamples.models.Sample bsdSample = toBsdSample.convert(usiSample);
            uk.ac.ebi.biosamples.models.Sample submitted = submit(bsdSample);
            if(submitted != null) {
                usiSample.setAccession(submitted.getAccession());
                usiSample.setStatus(ProcessingStatus.Done);
            } else {
                usiSample.setStatus(ProcessingStatus.ActionRequired);
            }
        });
        return sampleList;
    }

    private uk.ac.ebi.biosamples.models.Sample submit(uk.ac.ebi.biosamples.models.Sample bsdSample) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiUrl)
                .build()
                .toUri();
        logger.debug("URI: " + uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<uk.ac.ebi.biosamples.models.Sample> requestEntity = new RequestEntity<>(bsdSample, headers, HttpMethod.POST, uri);
        ResponseEntity<uk.ac.ebi.biosamples.models.Sample> responseEntity = restTemplate.exchange(requestEntity, uk.ac.ebi.biosamples.models.Sample.class);

        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Unable to POST:" + responseEntity.toString());
            return null;
        }
        logger.info("Submitted sample [" + responseEntity.getBody().getAccession() + "]");
        return responseEntity.getBody();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
