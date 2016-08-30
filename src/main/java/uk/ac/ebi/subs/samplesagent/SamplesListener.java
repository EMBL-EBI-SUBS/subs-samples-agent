package uk.ac.ebi.subs.samplesagent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submission;
import uk.ac.ebi.subs.messaging.Channels;
import uk.ac.ebi.subs.samplesrepo.SampleService;

import java.util.List;

@Service
public class SamplesListener {

    private static int i = 0;

    private RabbitMessagingTemplate rabbitTemplate;

    private SampleService sampleService;

    @Autowired
    public SamplesListener(RabbitMessagingTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }

    @Autowired
    public void setSampleService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @RabbitListener(queues = Channels.SAMPLES_PROCESSING)
    public void handleSubmission(Submission submission) {

        List<Sample> samples = handleSamples(submission); // Accessioning samples

        if(!samples.isEmpty()) {
            sampleService.saveSamples(samples);
        }

        //Send back to SUBMISSION_PROCESSED queue
        rabbitTemplate.convertAndSend(Channels.SUBMISSION_PROCESSED, submission);
    }

    public List<Sample> handleSamples(Submission submission) {

        List<Sample> samples = submission.getSamples();
        if (!samples.isEmpty()) {
            for (Sample sample : samples) {
                sample.setAccession(generateSampleAccession());
            }
        }
        return samples;
    }

    private String generateSampleAccession() {
        return "S" + ++i;
    }
}
