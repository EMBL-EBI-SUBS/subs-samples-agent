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
import uk.ac.ebi.subs.samplesrepo.SampleRepository;

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

        processSamples(submission);

        submissionEnvelope.addHandler(this.getClass());

        logger.info("processed submission {}",submission.getId());

        rabbitTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SUBMISSION_PROCESSED, submissionEnvelope);

        logger.info("sent submission {}",submission.getId());
    }

    private void processSamples(Submission submission) {
        List<Sample> samples = submission.getSamples();
        samples.forEach(sample -> {
            sample.setAccession(generateSampleAccession());
            sample.setStatus("ok");
        });

        repository.save(samples);
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
