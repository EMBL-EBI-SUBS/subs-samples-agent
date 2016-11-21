package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.agent.services.SubmissionService;
import uk.ac.ebi.subs.agent.services.SupportingSamplesService;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;

@Service
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    SupportingSamplesService supportingSamplesService;
    @Autowired
    SubmissionService submissionService;

    @Autowired
    public Listener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSamplesSubmission(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();
        logger.debug("Received new submission {" + submission.getId() + "}");

        // TODO - new submission
        submissionService.submit(envelope);

        // TODO - update
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void fetchSupportingSamples(SubmissionEnvelope envelope) {
        logger.debug("Received supporting samples request from submission {" + envelope.getSubmission().getId() + "}");

        List<Sample> samples = supportingSamplesService.findSamples(envelope);

        envelope.setSupportingSamples(samples);
        envelope.getSupportingSamplesRequired().clear();

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, envelope);

        logger.debug("Supporting samples provided for submission {" + envelope.getSubmission().getId() + "}");
    }
}
