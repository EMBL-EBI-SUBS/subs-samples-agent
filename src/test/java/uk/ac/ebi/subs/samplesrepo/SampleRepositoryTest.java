package uk.ac.ebi.subs.samplesrepo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
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

    private static MongoClient client;

    @Before //This runs before each test
    public void setUp() {
        client = new MongoClient();
        MongoDatabase db = client.getDatabase("test");
        MongoCollection collection = db.getCollection("sample");
        collection.drop();
    }

    @Test
    public void testSaveSample() {
        repository.save(Helpers.generateTestSample());
    }

    @Test
    public void testSaveMultipleSamples() {
        repository.save(Helpers.generateTestSamples());
    }

    @Test
    public void testGetSampleByAccession() {
        List<Sample> samples = Helpers.generateTestSamples();
        repository.save(samples);

        repository.findByAccession("S1");

    }

    @Test
    public void testGetAllSamples() {
        List<Sample> samples = repository.findAll();
        System.out.println(samples);
    }

}
