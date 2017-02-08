package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Sample;
import uk.ac.ebi.subs.agent.converters.BsdSampleToUsiSample;
import uk.ac.ebi.subs.agent.services.SubmissionService;
import uk.ac.ebi.subs.agent.services.SupportingSamplesService;
import uk.ac.ebi.subs.agent.services.UpdateService;
import uk.ac.ebi.subs.data.FullSubmission;
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
    UpdateService updateService;
    @Autowired
    BsdSampleToUsiSample biosampleToUsisample;

    @Autowired
    public Listener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSamplesSubmission(SubmissionEnvelope envelope) {
        FullSubmission submission = envelope.getSubmission();
        logger.debug("Received submission {" + submission.getId() + "}");

        List<uk.ac.ebi.subs.data.submittable.Sample> samples = submission.getSamples();

        if(isNewSubmission(samples)) {
            submissionService.submit(envelope);
        }

        updateService.update(envelope);
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void fetchSupportingSamples(SubmissionEnvelope envelope) {
        logger.debug("Received supporting samples request from submission {" + envelope.getSubmission().getId() + "}");

        List<Sample> biosamples = supportingSamplesService.findSamples(envelope);

        List<uk.ac.ebi.subs.data.submittable.Sample> usiSamples = biosampleToUsisample.convert(biosamples);

        envelope.setSupportingSamples(usiSamples);
        envelope.getSupportingSamplesRequired().clear();

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, envelope);

        logger.debug("Supporting samples provided for submission {" + envelope.getSubmission().getId() + "}");
    }

    private boolean isNewSubmission(List<uk.ac.ebi.subs.data.submittable.Sample> samples) {
        if (samples.get(0).getAccession() == null  || samples.get(0).getAccession().isEmpty()) {
            return true;
        }
        return false;
    }
}