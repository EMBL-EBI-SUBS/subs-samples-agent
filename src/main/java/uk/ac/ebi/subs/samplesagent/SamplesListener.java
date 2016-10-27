package uk.ac.ebi.subs.samplesagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.SubmissionEnvelope;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.AgentResults;
import uk.ac.ebi.subs.processing.Certificate;
import uk.ac.ebi.subs.processing.ProcessingStatus;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SamplesListener {

    private static final Logger logger = LoggerFactory.getLogger(SamplesListener.class);

    @Autowired
    private SampleRepository repository;

    private RabbitMessagingTemplate rabbitTemplate;

    private static int i = 0;

    @Autowired
    public SamplesListener(RabbitMessagingTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSubmission(SubmissionEnvelope submissionEnvelope) {
        Submission submission = submissionEnvelope.getSubmission();

        logger.info("received submission {}, most recent handler was {}",
                submission.getId(),
                submissionEnvelope.mostRecentHandler());

        List<Certificate> certs = processSamples(submission);

        submissionEnvelope.addHandler(this.getClass());

        logger.info("processed submission {}",submission.getId());

        AgentResults agentResults = new AgentResults(
                submissionEnvelope.getSubmission().getId(),
                certs
        );

        rabbitTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SUBMISSION_PROCESSED, agentResults);

        logger.info("sent submission {}",submission.getId());
    }

    private List<Certificate> processSamples(Submission submission) {
        List<Certificate> certs = new ArrayList<>();

        List<Sample> samples = submission.getSamples();

        samples.forEach(sample -> {
            sample.setAccession(generateSampleAccession());
            sample.setStatus(ProcessingStatus.Processed.toString());

            certs.add(new Certificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatus.Processed,
                    sample.getAccession())
            ); //TODO switch to UUID instead of alias asap
        });

        repository.save(samples);

        return certs;
    }

    protected void fillInSamples(SubmissionEnvelope envelope){
        for (SampleRef sampleRef : envelope.getSupportingSamplesRequired()){

            Sample sample = repository.findByAccession(sampleRef.getAccession());

            if (sample != null){
                envelope.getSupportingSamples().add(sample);
            }

        }


        envelope.getSupportingSamplesRequired().clear();
    }


    private String generateSampleAccession() {
        return "S" + ++i;
    }
}
