package uk.ac.ebi.subs.agent.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.client.BioSamplesClient;
import uk.ac.ebi.biosamples.client.BioSamplesClientConfig;
import uk.ac.ebi.biosamples.client.ClientProperties;
import uk.ac.ebi.subs.BioSamplesDependentTest;
import uk.ac.ebi.subs.agent.converters.BsdAttributeToUsiAttribute;
import uk.ac.ebi.subs.agent.converters.BsdRelationshipToUsiRelationship;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.exceptions.SampleNotFoundException;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        FetchService.class,
        BioSamplesClient.class,
        ClientProperties.class,
        BioSamplesClientConfig.class,
        RestOperations.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class,
        BsdRelationshipToUsiRelationship.class
})
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
public class FetchServiceTest {

    @Autowired
    FetchService service;

    private String accession;

    private SubmissionEnvelope envelope;
    private FullSubmission fullSubmission;
    private Submission submission;
    private SampleRef sampleRef;

    @Before
    public void setUp() {
        sampleRef = new SampleRef();
        sampleRef.setAccession(accession);

        submission = new Submission();
        submission.setId(UUID.randomUUID().toString());
        fullSubmission = new FullSubmission(submission);

        envelope = new SubmissionEnvelope();
        envelope.setSubmission(fullSubmission);
        envelope.setSupportingSamplesRequired(Sets.newSet(sampleRef));
    }

    @Test
    @Category(BioSamplesDependentTest.class)
    public void successfulSupportingSamplesServiceTest() {
        /*
        List<Sample> sampleList = null;
        try {
            sampleList = service.findSamples(envelope);
        } catch (SampleNotFoundException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println(sampleList.get(0));
        Assert.assertNotNull(sampleList);
        */
    }

    @Test
    @Category(BioSamplesDependentTest.class)
    public void sampleNotFoundTest() {
        /*
        envelope.getSupportingSamplesRequired().iterator().forEachRemaining(s -> s.setAccession("SAM"));

        List<Sample> sampleList = null;
        try {
            sampleList = service.findSamples(envelope);
        } catch (SampleNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(null, sampleList);
        */
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }
}