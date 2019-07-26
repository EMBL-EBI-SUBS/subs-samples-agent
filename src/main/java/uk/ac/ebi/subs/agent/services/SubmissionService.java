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
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.agent.utils.SampleSubmissionResponse;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This service is responsible to submit a list of {@link Sample} to the BioSamples archive.
 * The sample could be a new sample or the user could update an already existing sample in the database.
 */
@Service
@ConfigurationProperties(prefix = "biosamples")
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    BioSamplesClient client;

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    public List<SampleSubmissionResponse> submit(List<Sample> sampleList, String jwt) {
        Objects.requireNonNull(sampleList);
        ArrayList<SampleSubmissionResponse> responseList = new ArrayList<>();

        for (Sample usiSample : sampleList) {
            SampleSubmissionResponse response = submit(usiSample, jwt);
            response.getSample().setId(usiSample.getId());
            responseList.add(response);
        }
        return responseList;
    }

    private SampleSubmissionResponse submit(Sample usiSample, String jwt) {
        logger.debug("Submitting sample.");
        SampleSubmissionResponse response;
        uk.ac.ebi.biosamples.model.Sample bsdSample = toBsdSample.convert(usiSample);

        try {
            Sample sample = toUsiSample.convert(client.persistSampleResource(bsdSample, jwt).getContent());
            response = new SampleSubmissionResponse(sample, null, ProcessingStatusEnum.Completed);
        } catch (HttpClientErrorException e) {
            logger.error("http client error " + e.getResponseBodyAsString(), e);
            response = new SampleSubmissionResponse(usiSample, "HTTP client error: " + e.getMessage(), ProcessingStatusEnum.Error);
        } catch (ResourceAccessException e) {
            logger.error("Failed to access resource", e);
            response = new SampleSubmissionResponse(usiSample, "Failed to access the resource: " + e.getMessage(), ProcessingStatusEnum.Error);
        } catch (Exception e) {
            logger.error("Failed to submit sample", e);
            response = new SampleSubmissionResponse(usiSample, "Failed to submit sample: " + e.getMessage(), ProcessingStatusEnum.Error);
        }

        return response;
    }
}
