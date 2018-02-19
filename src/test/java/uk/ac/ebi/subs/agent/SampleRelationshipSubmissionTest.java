package uk.ac.ebi.subs.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import uk.ac.ebi.subs.SamplesAgentApplication;
import uk.ac.ebi.subs.agent.utils.BioSamplesDependentTest;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
      /*  SamplesProcessor.class,
        FetchService.class,
        IntegrityService.class,
        UpdateService.class,
        SubmissionService.class,
        CertificatesGenerator.class,
        UsiSampleToBsdSample.class,
        UsiAttributeToBsdAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class,
        BsdRelationshipToUsiRelationship.class,
        TestUtils.class,
        RestOperations.class*/
        SamplesAgentApplication.class
})
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
@Category(BioSamplesDependentTest.class)
public class SampleRelationshipSubmissionTest {

    @Autowired
    SamplesProcessor samplesProcessor;

    @Autowired
    TestUtils utils;

    private Sample parentSample;
    private Sample childSample;
    private SubmissionEnvelope submissionEnvelope;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;
    @MockBean(name = "messageConverter")
    MessageConverter messageConverter;

    @Before
    public void setUp() {
        parentSample = utils.generateUsiSample();
        parentSample.setAlias("p1");
        parentSample.setAccession(null);
        parentSample.setId("uuid-1");
        childSample = utils.generateUsiSample();
        childSample.setAlias("c1");
        childSample.setAccession(null);
        childSample.setId("uuid-2");

        SampleRelationship sr = new SampleRelationship();
        sr.setAlias("p1");
        sr.setRelationshipNature("child of");
        childSample.setSampleRelationships(Arrays.asList(sr));

        submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.setSubmission(new Submission());
        submissionEnvelope.getSubmission().setId("1234");
        submissionEnvelope.setSamples(Arrays.asList(childSample, parentSample));
    }

    @Test
    public void submitTest() {
        List<ProcessingCertificate> certList = null;
        try {
            certList = samplesProcessor.processSamples(submissionEnvelope);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        assertNotNull(certList);
    }

}
