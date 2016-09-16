package uk.ac.ebi.subs.samplesagent;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
@SpringBootTest(classes = {QueueListenerTestConfiguration.class, SamplesListener.class})
public class QueueListenerTest {

    @Autowired
    static RabbitMessagingTemplate rabbitMessagingTemplate;

    @BeforeClass
    public static void startUp() {
        
    }
/*
    @AfterClass
    public static void tearDown() {

    }
*/
    @Test
    public void testSendMessage() {
        // TODO
    }

    @Test
    public void testReceiveMessage() {
        //TODO
    }

}
