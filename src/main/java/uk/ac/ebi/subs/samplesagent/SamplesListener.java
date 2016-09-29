package uk.ac.ebi.subs.samplesagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submission;
import uk.ac.ebi.subs.messaging.Channels;
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

    @RabbitListener(queues = Channels.SAMPLES_PROCESSING)
    public void handleSubmission(Submission submission) {
        logger.info("received submission {}",submission.getId());

        processSamples(submission);

        logger.info("processed submission {}",submission.getId());

        rabbitTemplate.convertAndSend(Channels.SUBMISSION_PROCESSED, submission);

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


    private String generateSampleAccession() {
        return "S" + ++i;
    }
}
