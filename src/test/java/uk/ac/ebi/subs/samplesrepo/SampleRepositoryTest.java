package uk.ac.ebi.subs.samplesrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = SampleRepositoryTestConfiguration.class)
public class SampleRepositoryTest {

    @Autowired
    SampleRepository repository;

    @Test
    public void testSaveSample() {
        repository.save(generateTestSample());
    }

    @Test
    public void testGetSampleByAccession() {
        Sample sample = repository.findByAccession("S1");
        System.out.println(sample);
    }

    @Test
    public void testSaveMultipleSamples() {
        repository.save(generateTestSamples());
    }

    @Test
    public void testGetAllSamples() {
        List<Sample> samples = repository.findAll();
        System.out.println(samples);
    }

    private Sample generateTestSample() {
        Sample sample = new Sample();
        sample.setAccession("S1");
        sample.setDescription("Test sample 1.");

        return sample;
    }

    private List<Sample> generateTestSamples() {
        List<Sample> samples = new ArrayList<>();
        Sample sample2 = new Sample();
        sample2.setAccession("S2");
        sample2.setDescription("Test sample 2.");
        samples.add(sample2);

        Sample sample3 = new Sample();
        sample3.setAccession("S3");
        samples.add(sample3);
        sample3.setDescription("Test sample 3.");

        return samples;
    }
}
