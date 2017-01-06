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
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
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
    private Submission submission;
    private SampleRef sampleRef;
    private Sample sample;

    @Before
    public void setUp() {
        sample = new Sample();
        sample.setAccession(accession);

        sampleRef = new SampleRef();
        sampleRef.setReferencedObject(sample);

        submission = new Submission();
        submission.setId(UUID.randomUUID().toString());

        envelope = new SubmissionEnvelope();
        envelope.setSubmission(submission);
        envelope.setSupportingSamplesRequired(Sets.newSet(sampleRef));
    }

    @Test
    public void SuccessfulSupportingSamplesServiceTest() {
        List<uk.ac.ebi.subs.agent.biosamples.Sample> sampleList = service.findSamples(envelope);
        System.out.println(sampleList.get(0));
        Assert.assertNotNull(sampleList);
    }

    public void SampleNotFoundTest() {
        envelope.getSupportingSamplesRequired().iterator().forEachRemaining(s -> s.setAccession("SAM"));
        List<uk.ac.ebi.subs.agent.biosamples.Sample> sampleList = service.findSamples(envelope);
        Assert.assertNull(sampleList);

    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }
}