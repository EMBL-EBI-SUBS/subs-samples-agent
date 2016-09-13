package uk.ac.ebi.subs.samplesrepo;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
//@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@ContextConfiguration
public class SampleRepositoryTest {

    @Autowired
    SampleRepository repository;

    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private static MongodExecutable executable;
    private static MongodProcess process;

    private static MongoClient client;

    @BeforeClass
    public static void setUp() throws Exception {
        executable = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.V3_2)
                .net(new Net(12345, Network.localhostIsIPv6()))
                .build());

        process = executable.start();

        client = new MongoClient("localhost", 12345);
    }

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

    @AfterClass
    public static void tearDown() throws Exception {
        client.close();
        process.stop();
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
