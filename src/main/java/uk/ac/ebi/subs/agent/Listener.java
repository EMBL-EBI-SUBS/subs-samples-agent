package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.agent.exceptions.SampleNotFoundException;
import uk.ac.ebi.subs.agent.services.SubmissionService;
import uk.ac.ebi.subs.agent.services.FetchService;
import uk.ac.ebi.subs.agent.services.UpdateService;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.processing.UpdatedSamplesEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    FetchService fetchService;
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
        ProcessingCertificateEnvelope certificateEnvelope = new ProcessingCertificateEnvelope(submission.getId());

        List<Sample> samplesToUpdate = getSamplesToUpdate(submission.getSamples()); // Update
        updateService.update(samplesToUpdate);
        List<Sample> handledSamples = new ArrayList<>(samplesToUpdate);

        List<Sample> samplesToSubmit = getSamplesToSubmit(submission.getSamples()); // Submit
        handledSamples.addAll(submissionService.submit(samplesToSubmit));

        UpdatedSamplesEnvelope updatedSamplesEnvelope = new UpdatedSamplesEnvelope(); // Updated envelope
        updatedSamplesEnvelope.setSubmissionId(submission.getId());
        updatedSamplesEnvelope.setUpdatedSamples(samplesToUpdate);

        List<ProcessingCertificate> processingCertificateList = generateCertificates(handledSamples); // Handled certificates
        certificateEnvelope.setProcessingCertificates(processingCertificateList);

        // TODO - if a sample has status = error

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SAMPLES_UPDATED, updatedSamplesEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelope);
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void fetchSupportingSamples(SubmissionEnvelope envelope) {
        FullSubmission submission = envelope.getSubmission();
        logger.debug("Received supporting samples request from submission [" + submission.getId() + "]");

        List<Sample> sampleList = null;
        try {
            sampleList = fetchService.findSamples(envelope);
        } catch (SampleNotFoundException e) {
            e.printStackTrace();
        }
        envelope.setSupportingSamples(sampleList);

        if(!envelope.getSupportingSamplesRequired().isEmpty()) {    // Missing required samples
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_NEEDS_SAMPLES, envelope);
        } else{
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, envelope);
            logger.debug("Supporting samples provided for submission {" + envelope.getSubmission().getId() + "}");
        }
    }

    private List<ProcessingCertificate> generateCertificates(List<Sample> sampleList) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleList.forEach(sample -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatusEnum.Accepted, // FIXME - infer correct status
                    sample.getAccession()
            );
            processingCertificateList.add(pc);
        });
        return processingCertificateList;
    }

    private List<Sample> getSamplesToUpdate(List<Sample> allSamples) {
        return allSamples
                .stream()
                .filter(sample -> sample.getAccession() != null && !sample.getAccession().isEmpty())
                .collect(Collectors.toList());
    }

    private List<Sample> getSamplesToSubmit(List<Sample> allSamples) {
        return allSamples
                .stream()
                .filter(sample -> sample.getAccession() == null || sample.getAccession().isEmpty())
                .collect(Collectors.toList());
    }
}