package uk.ac.ebi.subs.samplesrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.util.Helpers;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = SampleRepositoryTestConfiguration.class)
public class SampleRepositoryTest {

    @Autowired
    SampleRepository repository;

    @Test
    public void testSaveSample() {
        repository.save(Helpers.generateTestSample());
    }

    @Test
    public void testGetSampleByAccession() {
        Sample sample = repository.findByAccession("S1");
        System.out.println(sample);
    }

    @Test
    public void testSaveMultipleSamples() {
        repository.save(Helpers.generateTestSamples());
    }

    @Test
    public void testGetAllSamples() {
        List<Sample> samples = repository.findAll();
        System.out.println(samples);
    }

}
