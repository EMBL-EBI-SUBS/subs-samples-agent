package uk.ac.ebi.subs.samplesagent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;
import uk.ac.ebi.subs.util.Helpers;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = {SamplesListenerTestConfiguration.class, SamplesListener.class})
public class SamplesListenerTest {

    @Autowired
    private SamplesListener samplesListener;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testSendMessage() {
        // TODO

    }

    @Test
    public void testSubmissionHandler() {
        mongoTemplate.getCollection("sample").drop();
        samplesListener.handleSubmission(Helpers.generateTestSubmission());
    }

}
