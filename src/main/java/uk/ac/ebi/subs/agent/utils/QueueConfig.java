package uk.ac.ebi.subs.agent.utils;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.messaging.Queues;

@Configuration
public class QueueConfig {
    /**
     * Routing key for a message coming from an agent that produced some accessionIDs
     */
    public static final String USI_ARCHIVE_ACCESSIONIDS_PUBLISHED_ROUTING_KEY = "usi.archiveaccessionids.published";
    public static final String USI_ARCHIVE_ACCESSIONIDS_PUBLISHED__QUEUE = "usi-archiveaccessionids-published-queue";

    /**
     * Queue to get accession IDs
     * @return a Queue instance for the accession queue
     */
    @Bean
    Queue accessionQueue() {
        return Queues.buildQueueWithDlx(USI_ARCHIVE_ACCESSIONIDS_PUBLISHED__QUEUE);
    }

    @Bean
    Binding accessionConsumingBinding(Queue accessionQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(accessionQueue).to(submissionExchange).with(USI_ARCHIVE_ACCESSIONIDS_PUBLISHED_ROUTING_KEY);
    }
}
