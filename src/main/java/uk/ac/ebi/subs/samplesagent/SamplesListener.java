package uk.ac.ebi.subs.samplesagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.processing.*;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.samplesrepo.SampleRepository;



import java.util.ArrayList;
import java.util.List;

@Service
public class SamplesListener {

    private static final Logger logger = LoggerFactory.getLogger(SamplesListener.class);

    @Autowired
    private SampleRepository repository;

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    private static int i = 0;

    @Autowired
    public SamplesListener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.SUBMISSION_NEEDS_SAMPLE_INFO)
    public void handleSuppInfoRequired(SubmissionEnvelope submissionEnvelope){
        fillInSamples(submissionEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SUBISSION_SUPPORTING_INFO_PROVIDED, submissionEnvelope);
    }


    @RabbitListener(queues = Queues.BIOSAMPLES_AGENT)
    public void handleSubmission(SubmissionEnvelope submissionEnvelope) {
        Submission submission = submissionEnvelope.getSubmission();

        logger.info("received submission {}, most recent handler was {}",
                submission.getId());

        List<Certificate> certs = processSamples(submission);

        fillInSamples(submissionEnvelope);

        logger.info("processed submission {}",submission.getId());

        AgentResults agentResults = new AgentResults(
                submissionEnvelope.getSubmission().getId(),
                certs
        );

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SUBMISSION_AGENT_RESULTS, agentResults);

        logger.info("sent submission {}",submission.getId());
    }

    private List<Certificate> processSamples(Submission submission) {
        List<Certificate> certs = new ArrayList<>();
        List<Sample> updatedSamples = new ArrayList<>();

        List<Sample> samples = submission.getSamples();

        samples.forEach(sample -> {

            boolean isUpdate = false;

            //if it already has an accession, it has been submitted before
            if (sample.isAccessioned()){
                isUpdate = true;
            }

            // if it has a domain and an alias, it might have been submitted already
            if (sample.getDomain() != null && sample.getDomain().getName() != null && sample.getAlias() != null){
                Sample existingSample = repository.findByDomainAndAlias(sample.getDomain().getName(),sample.getAlias());
                if (existingSample != null){
                    sample.setAccession(existingSample.getAccession());
                    isUpdate = true;
                }
            }

            //if it hasn't been submitted before, it needs an accession
            if (!isUpdate){
                sample.setAccession(generateSampleAccession());
            }

            // track that we're updating this sample, need to do something with it later
            if (isUpdate){
                updatedSamples.add(sample);
            }

            sample.setStatus(ProcessingStatus.Processed.toString());

            certs.add(new Certificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatus.Processed,
                    sample.getAccession())
            );

        });

        repository.save(samples);

        announceSampleUpdate(submission.getId(),updatedSamples);


        return certs;
    }

    protected void announceSampleUpdate(String submissionId, List<Sample> updatedSamples){

        if (updatedSamples.isEmpty()) return;

        UpdatedSamplesEnvelope updatedSamplesEnvelope = new UpdatedSamplesEnvelope();
        updatedSamplesEnvelope.setSubmissionId(submissionId);
        updatedSamplesEnvelope.setUpdatedSamples(updatedSamples);

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SAMPLES_UPDATED,updatedSamplesEnvelope);

    }



    protected void fillInSamples(SubmissionEnvelope envelope){
        //TODO this should do something in case of errors
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
