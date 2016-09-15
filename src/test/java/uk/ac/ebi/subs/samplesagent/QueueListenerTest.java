package uk.ac.ebi.subs.samplesagent;

import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SamplesQueueListenerTestConfiguration.class)
public class SamplesQueueListenerTest {

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    
}
