package uk.ac.ebi.subs.samplesagent.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Submission;
import uk.ac.ebi.subs.messaging.Channels;

@Service
public class SubmissionReceiver {

    @RabbitListener(queues = Channels.SUBMISSION_SUBMITTED)
    public void handleSubmission(Submission submission) {
        //TODO receive and store submission
    }
}
