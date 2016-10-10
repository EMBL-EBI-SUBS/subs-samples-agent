package uk.ac.ebi.subs.samplesagent;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.SubmissionEnvelope;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;
import uk.ac.ebi.subs.util.Helpers;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = {SamplesListenerTestConfiguration.class, SamplesListener.class})
public class SampleListenerReadSamplesTest {

    @Autowired
    SamplesListener samplesListener;

    @Autowired
    SampleRepository sampleRepository;

    List<Sample> samples;
    SubmissionEnvelope submissionEnvelope;

    @Test
    public void findTheSamples(){
        samplesListener.fillInSamples(submissionEnvelope);

        assertThat("list of supporting samples required is empty", submissionEnvelope.getSupportingSamplesRequired(), empty());
        assertThat("list of supporting samples is the right size", submissionEnvelope.getSupportingSamples(),hasSize(samples.size()));
        assertThat("list of supporting samples has the correct samples in it", submissionEnvelope.getSupportingSamples(),containsInAnyOrder(samples.toArray()));
    }


    @Before
    public void setUp() {
        sampleRepository.deleteAll();

        submissionEnvelope = new SubmissionEnvelope();

        samples = Helpers.generateTestSamples();

        List<SampleRef> sampleRefs = samples.stream()
                .map(s -> (SampleRef)s.asRef())
                .collect(Collectors.toList());
        submissionEnvelope.getSupportingSamplesRequired().addAll(sampleRefs);

        samples = Helpers.generateTestSamples();

        sampleRepository.save(samples);
    }

    @After
    public void tearDown(){
        sampleRepository.deleteAll();
    }

}
