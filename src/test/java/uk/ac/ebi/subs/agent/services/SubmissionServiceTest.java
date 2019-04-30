package uk.ac.ebi.subs.agent.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import uk.ac.ebi.biosamples.client.service.AapClientService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        SubmissionService.class,
        UsiSampleToBsdSample.class,
        UsiAttributeToBsdAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdSampleToUsiSample.class,
        BsdAttributeToUsiAttribute.class,
        BsdRelationshipToUsiRelationship.class,
        TestUtils.class,
        RestOperations.class
})
@ConfigurationProperties(prefix = "test")
@EnableAutoConfiguration
@Category(BioSamplesDependentTest.class)
public class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    TestUtils utils;

    @Autowired
    AapClientService aapClientService;

    private Sample sample;
    private Sample sampleToUpdate;
    private String jwt;

    @Before
    public void setUp() {
        sample = utils.generateUsiSampleForSubmission();
        sampleToUpdate = utils.generateUsiSampleForUpdate();
        jwt = aapClientService.getJwt();
    }

    @Test
    public void testJwtNotNull() {
        assertFalse("JWT shouldn't be empty or null", jwt == null || jwt.equals(""));
    }

    @Test
    public void whenSubmittingASampleWithoutTaxonAndTaxonId_ThenSubmissionShouldBeSuccessful() {
        List<Sample> sampleList = null;
        sample.setTaxonId(null);
        sample.setTaxon(null);
        try {
            sampleList = submissionService.submit(Collections.singletonList(sample), jwt);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        assertNotNull(sampleList);
        assertNull(sampleList.get(0).getTaxonId());
        assertNull(sampleList.get(0).getTaxon());
    }


    @Test
    public void whenSubmittingASampleWithTaxonButWithoutTaxonId_ThenSubmissionShouldBeSuccessful() {
        List<Sample> sampleList = null;
        sample.setTaxonId(null);
        try {
            sampleList = submissionService.submit(Collections.singletonList(sample), jwt);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        assertNotNull(sampleList);
        assertNull(sampleList.get(0).getTaxonId());
        assertNotNull(sampleList.get(0).getTaxon());
    }

    @Test
    public void whenSubmittingASampleWithoutTaxonButWithTaxonID_ThenSubmissionShouldBeSuccessful() {
        List<Sample> sampleList = null;
        sample.setTaxon(null);
        try {
            sampleList = submissionService.submit(Collections.singletonList(sample), jwt);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        assertNotNull(sampleList);
        assertThat(sampleList.get(0).getTaxon(), is(emptyString()));
        assertNotNull(sampleList.get(0).getTaxonId());
    }

    @Test
    public void whenSubmittingASampleWithTaxonAndTaxonId_ThenSubmissionShouldBeSuccessful() {
        List<Sample> sampleList = null;
        try {
            sampleList = submissionService.submit(Collections.singletonList(sample), jwt);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        assertNotNull(sampleList);
        assertNotNull(sampleList.get(0).getTaxon());
        assertNotNull(sampleList.get(0).getTaxonId());
    }

    @Test
    @Category(BioSamplesDependentTest.class)
    public void update() {
        List<Sample> updated = submissionService.submit(Arrays.asList(sampleToUpdate), jwt);
        Assert.assertEquals(updated.get(0).getAccession(), sampleToUpdate.getAccession());
    }
}
