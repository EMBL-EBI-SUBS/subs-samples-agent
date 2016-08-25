package uk.ac.ebi.subs.samplesagent.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submission;
import uk.ac.ebi.subs.messaging.Channels;

import java.util.List;

@Service
public class SubmissionReceiver {

    private static int i = 0;

    @RabbitListener(queues = Channels.SAMPLES_PROCESSING)
    public void handleSubmission(Submission submission) {

        boolean status = handleSamples(submission); // setting accessions

        //TODO save submission


        //TODO send back to SUBMISSION_PROCESSED queue
        String targetQueue = Channels.SUBMISSION_PROCESSED;

    }

    private boolean handleSamples(Submission submission) {
        try {
            List<Sample> samples = submission.getSamples();
            if (!samples.isEmpty()) {
                for (Sample sample : samples) {
                    sample.setAccession(generateSampleAccession());
                }
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    private String generateSampleAccession() {
        return "S" + ++i;
    }
}
