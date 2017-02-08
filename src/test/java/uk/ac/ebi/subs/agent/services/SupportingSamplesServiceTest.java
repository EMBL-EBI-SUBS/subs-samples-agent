package uk.ac.ebi.subs.agent.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.biosamples.models.*;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SupportingSamplesService.class)
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
public class SupportingSamplesServiceTest {

    @Autowired
    SupportingSamplesService service;

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
    public void successfulSupportingSamplesServiceTest() {
        List<Sample> sampleList = null;
        try {
            sampleList = service.findSamples(envelope);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        System.out.println(sampleList.get(0));
        Assert.assertNotNull(sampleList);
    }

    @Test
    public void sampleNotFoundTest() {
        envelope.getSupportingSamplesRequired().iterator().forEachRemaining(s -> s.setAccession("SAM"));

        List<Sample> sampleList = null;
        try {
            sampleList = service.findSamples(envelope);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertNull(sampleList);
        }
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }
}