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
import uk.ac.ebi.biosamples.client.service.AapClientService;
import uk.ac.ebi.subs.agent.utils.BioSamplesDependentTest;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@Category(BioSamplesDependentTest.class)
public class IntegrityServiceTest {

    @Autowired
    private IntegrityService integrityService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    AapClientService aapClientService;

    private Sample sample;
    private String accessionId;
    private String jwt;

    @Before
    public void setUp() throws Exception {
        List<Sample> sampleList = new ArrayList<>();
        sample = testUtils.generateUsiSampleForSubmission();

        try {
        jwt = aapClientService.getJwt();
            sampleList = submissionService.submit(Collections.singletonList(sample), jwt);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }

        accessionId = sampleList.get(0).getAccession();
    }

    @Test
    public void sampleDoesExistTest() {
       assertTrue(integrityService.doesSampleExistInBioSamples(accessionId));
    }

    @Test
    public void sampleDoesNotExistTest() {
        String randomAccessionId = UUID.randomUUID().toString() + "SHOULD_NOT_EXISTS";
        assertTrue(!integrityService.doesSampleExistInBioSamples(randomAccessionId));
    }
}
