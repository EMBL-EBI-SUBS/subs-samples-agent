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
import uk.ac.ebi.subs.agent.services.UpdateService;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    SupportingSamplesService supportingSamplesService;
    @Autowired
    SubmissionService submissionService;
    @Autowired
    UpdateService updateService;

    @Autowired
    public Listener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSamplesSubmission(SubmissionEnvelope envelope) {
        FullSubmission submission = envelope.getSubmission();
        logger.debug("Received submission [" + submission.getId() + "]");
        List<Sample> completeSampleList = new ArrayList<>(submission.getSamples());
        ProcessingCertificateEnvelope certificateEnvelope = new ProcessingCertificateEnvelope(submission.getId());

        // Update existing samples
        List<Sample> existingSamples = completeSampleList
                .parallelStream()
                .filter(sample -> sample.getAccession() != null && !sample.getAccession().isEmpty())
                .collect(Collectors.toList());
        updateService.update(existingSamples);
        List<Sample> handledSamples = new ArrayList<>(existingSamples);

        // Submit new samples
        List<Sample> newSamples = completeSampleList
                .parallelStream()
                .filter(sample -> sample.getAccession() == null || sample.getAccession().isEmpty())
                .collect(Collectors.toList());
        handledSamples.addAll(submissionService.submit(newSamples));

        List<ProcessingCertificate> processingCertificateList = generateCertificates(handledSamples);
        certificateEnvelope.setProcessingCertificates(processingCertificateList);

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelope);
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void fetchSupportingSamples(SubmissionEnvelope envelope) {
        FullSubmission submission = envelope.getSubmission();
        logger.debug("Received supporting samples request from submission [" + submission.getId() + "]");

        List<Sample> sampleList = supportingSamplesService.findSamples(envelope);
        envelope.setSupportingSamples(sampleList);

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, envelope);
        logger.debug("Supporting samples provided for submission {" + envelope.getSubmission().getId() + "}");
    }

    private List<ProcessingCertificate> generateCertificates(List<Sample> sampleList) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleList.forEach(sample -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatus.valueOf(sample.getStatus())
            );
            processingCertificateList.add(pc);
        });
        return processingCertificateList;
    }
}