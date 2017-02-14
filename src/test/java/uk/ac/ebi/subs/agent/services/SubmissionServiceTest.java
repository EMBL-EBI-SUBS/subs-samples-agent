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
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.agent.converters.BsdAttributeToUsiAttribute;
import uk.ac.ebi.subs.agent.converters.BsdRelationshipToUsiRelationship;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiAttributeToBsdAttribute;
import uk.ac.ebi.subs.agent.converters.UsiRelationshipToBsdRelationship;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        SubmissionService.class,
        UsiSampleToBsdSample.class,
        UsiAttributeToBsdAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class,
        BsdRelationshipToUsiRelationship.class,
        RestTemplate.class,
        TestUtils.class
})
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
public class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    TestUtils utils;

    private Sample sample;

    @Before
    public void setUp() {
        sample = utils.generateUsiSampleForSubmission();
    }
/*
    @Test
    public void submit() {
        List<Sample> sampleList = null;
        try {
            sampleList = submissionService.submit(Arrays.asList(sample));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        sampleList.forEach(sample -> Assert.assertNotNull(sample.getAccession()));
    }
*/
}
