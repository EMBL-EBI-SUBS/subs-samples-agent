package uk.ac.ebi.subs.samplesrepo;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:test.properties") // Used to override the properties set on application.properties
public class SamplesRepositoryTest {

    @Autowired
    private SampleRepository repository;

/*
    @Test
    public void testSaveSample() {

    }

    @Test
    public void testGetSample() {

    }

    @Test
    public void testGetAllSamples() {

    }
*/


    private List<Sample> generateTestSamples() {
        List<Sample> samples = new ArrayList<>();
        Sample sample1 = new Sample();
        sample1.setAccession("S1");
        samples.add(sample1);
        sample1.setDescription("Test sample 1.");

        Sample sample2 = new Sample();
        sample2.setAccession("S2");
        sample2.setDescription("Test sample 2.");
        samples.add(sample2);

        return samples;
    }
}
