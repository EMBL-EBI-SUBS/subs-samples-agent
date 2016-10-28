package uk.ac.ebi.subs.samplesagent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.processing.AgentResults;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;
import uk.ac.ebi.subs.util.Helpers;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = {SamplesListenerTestConfiguration.class, SamplesListener.class})
public class SamplesListenerTest {

    @Autowired
    SamplesListener samplesListener;

    @Autowired
    RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    MessageConverter messageConverter;

    @Autowired
    MongoTemplate mongoTemplate;

    Submission submission;
    int messages = 0;

    @Before
    public void setUp() {
        this.mongoTemplate.getCollection("sample").drop();
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);

        this.messages = 0;

        submission = Helpers.generateTestSubmission();
    }

    @Test
    public void testSubmissionHandler() throws InterruptedException {
        samplesListener.handleSubmission(new SubmissionEnvelope(Helpers.generateTestSubmission())); // Test submission with 3 samples

        Thread.sleep(1000);
        System.out.println("Messages: " + messages);
        System.out.println("Samples found in DB: " + mongoTemplate.getCollection("sample").count());
    }
    

    @RabbitListener(queues = Queues.SUBMISSION_MONITOR)
    public void listener(AgentResults agentResults) {
        synchronized (this) {
            System.out.println(submission.toString());
            System.out.println("Message received!");
        }
    }
}
