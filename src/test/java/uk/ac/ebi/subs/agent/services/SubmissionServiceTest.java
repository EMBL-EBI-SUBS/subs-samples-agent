package uk.ac.ebi.subs.agent.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.agent.converters.BsdAttributeToUsiAttribute;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiAttributeToBsdAttribute;
import uk.ac.ebi.subs.agent.converters.UsiRelationshipToBsdRelationship;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        SubmissionService.class,
        UsiSampleToBsdSample.class,
        UsiAttributeToBsdAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class
})
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
public class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

    private SubmissionEnvelope envelope;
    private FullSubmission fullSubmission;
    private Submission submission;
    private Sample sample;

    @Before
    public void setUp() {
        submission = new Submission();
        submission.setId(UUID.randomUUID().toString());
        fullSubmission = new FullSubmission(submission);

        sample = generateSample();
        fullSubmission.setSamples(Arrays.asList(sample));

        envelope = new SubmissionEnvelope();
        envelope.setSubmission(fullSubmission);
    }

    @Test
    public void submit() {
        List<Sample> sampleList = null;
        try {
            sampleList = submissionService.submit(envelope);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        sampleList.forEach(System.out::println);
        Assert.assertNotNull(sampleList);
    }

    private Sample generateSample() {
        Sample s = new Sample();
        s.setArchive(Archive.BioSamples);
        s.setTitle("Experiment on mice.");
        s.setTaxonId(10090L);
        s.setTaxon("Mus musculus");
        s.setDescription("Sample from Mus musculus.");
        s.setId(UUID.randomUUID().toString());

        Attribute att = new Attribute();
        att.setName("synonym");
        att.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        att.setTerms(Arrays.asList(t));

        s.setAttributes(Arrays.asList(att));

        return s;
    }
}
