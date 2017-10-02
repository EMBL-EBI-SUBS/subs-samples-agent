package uk.ac.ebi.subs.agent.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import uk.ac.ebi.subs.agent.converters.BsdAttributeToUsiAttribute;
import uk.ac.ebi.subs.agent.converters.BsdRelationshipToUsiRelationship;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.converters.UsiAttributeToBsdAttribute;
import uk.ac.ebi.subs.agent.converters.UsiRelationshipToBsdRelationship;
import uk.ac.ebi.subs.agent.converters.UsiSampleToBsdSample;
import uk.ac.ebi.subs.agent.utils.BioSamplesDependentTest;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        FetchService.class,
        RestOperations.class,
        SubmissionService.class,
        UsiSampleToBsdSample.class,
        UsiAttributeToBsdAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class,
        BsdRelationshipToUsiRelationship.class,
        TestUtils.class,
})
@EnableAutoConfiguration
@Category(BioSamplesDependentTest.class)
public class FetchServiceTest {

    @Autowired
    FetchService service;

    @Autowired
    SubmissionService submissionService;

    @Autowired
    TestUtils utils;

    Sample sample;
    List<Sample> submitted;

    @Before
    public void setUp() {
        sample = utils.generateUsiSampleForSubmission();
        submitted = submissionService.submit(Arrays.asList(sample));
    }

    @Test
    public void successfulSupportingSamplesServiceTest() {
        List<Sample> sampleList;
        try {
            sampleList = service.findSamples(Arrays.asList(submitted.get(0).getAccession()));
        } catch (HttpClientErrorException exception) {
            System.out.println(exception.getResponseBodyAsString());
            throw exception;
        }
        assertNotNull(sampleList);
    }

    @Test
    public void sampleNotFoundTest() {
        List<Sample> sampleList = service.findSamples(Arrays.asList("SAM"));
        assertTrue(sampleList.isEmpty());
    }
}