package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.agent.utils.QueueConfig;
import uk.ac.ebi.subs.agent.utils.SampleSubmissionResponse;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.AccessionIdEnvelope;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains 2 listeners on different RabbitMQ queues.
 * One is handling the submitted samples and sending the processing certificates after completion of the sample submission.
 * The other is fetching sample information from the BioSamples database to support other sample's submission.
 */
@Service
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    SamplesProcessor samplesProcessor;

    @Autowired
    CertificatesGenerator certificatesGenerator;

    @Autowired
    public Listener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSamplesSubmission(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();

        logger.info("Received submission {}", submission.getId());

        List<ProcessingCertificate> certificatesCompleted = samplesProcessor.processSamples(envelope);

        if (!certificatesCompleted.isEmpty()) {
            ProcessingCertificateEnvelope certificateEnvelopeCompleted = new ProcessingCertificateEnvelope(
                    submission.getId(),
                    certificatesCompleted,
                    envelope.getJWTToken()
            );
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelopeCompleted);
        }

        logger.info("Processed submission {}", submission.getId());
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void fetchSupportingSamples(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();

        logger.debug("Received supporting samples request from submission {}", submission.getId());

        List<Sample> sampleList = samplesProcessor.findSamples(envelope);
        envelope.setSupportingSamples(sampleList);
        envelope.getSupportingSamplesRequired().clear();

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, envelope);
        logger.debug("Supporting samples provided for submission {}", submission.getId());
    }

    @RabbitListener(queues = QueueConfig.USI_ARCHIVE_ACCESSIONIDS_PUBLISHED__QUEUE)
    public void fetchAccessionUpdateMessage(AccessionIdEnvelope envelope) {
        logger.debug("Received accession update message {}, {}",
                envelope.getBioStudiesAccessionId(), envelope.getBioSamplesAccessionIds());

        List<SampleSubmissionResponse> sampleResponseList = samplesProcessor.updateAccessions(
                envelope.getBioSamplesAccessionIds(), envelope.getBioStudiesAccessionId(), null);

        logger.debug("updated samples/total samples {}/{}",
                sampleResponseList.stream().filter(s -> s.getMessage() == null).count(),
                envelope.getBioSamplesAccessionIds().size());
    }
}