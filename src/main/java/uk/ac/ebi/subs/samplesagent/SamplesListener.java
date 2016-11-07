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


import java.util.*;
import java.util.stream.Collectors;

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

        // these samples must be updates, as they are already accessioned
        List<Sample> accessionedSamples = submission.getSamples().stream().filter(s -> s.getAccession() != null).collect(Collectors.toList());

        //these samples might be updates, if the alias+domain are already used
        List<Sample> samplesWithoutAccession = submission.getSamples().stream().filter(s -> s.getAccession() == null).collect(Collectors.toList());

        //we need the aliases in an array to make a bulk query
        String[] aliasesForSamplesWithoutAccession = new String[0];
        aliasesForSamplesWithoutAccession = samplesWithoutAccession.stream().map(s -> s.getAlias()).collect(Collectors.toList()).toArray(aliasesForSamplesWithoutAccession);

        // check the db for these aliases, store any by alias
        Map<String,Sample> knownSamplesByAlias = new HashMap<>();
        repository.findByDomainAndAlias(submission.getDomain().getName(),aliasesForSamplesWithoutAccession).forEach(
                sample -> knownSamplesByAlias.put(sample.getAlias(),sample)
        );

        // check for an accession for the samples without accession; if you find one this is an update
        ListIterator<Sample> samplesWithoutAccessionListIter = samplesWithoutAccession.listIterator();
        while (samplesWithoutAccessionListIter.hasNext()){
            Sample sample = samplesWithoutAccessionListIter.next();

            if (knownSamplesByAlias.containsKey(sample.getAlias())){
                sample.setAccession(knownSamplesByAlias.get(sample.getAlias()).getAccession());
                samplesWithoutAccessionListIter.remove();
                accessionedSamples.add(sample);
            }
            else {
                sample.setAccession(generateSampleAccession());
            }
        }

        submission.getSamples().forEach(s -> {
            s.setStatus(ProcessingStatus.Processed.toString());

            certs.add(new Certificate(
                    s,
                    Archive.BioSamples,
                    ProcessingStatus.Processed,
                    s.getAccession())
            );

        });
        logger.info("submission {} has {} new samples and {} to update",submission.getId(),samplesWithoutAccession.size(),accessionedSamples.size());
        repository.save(submission.getSamples());


        announceSampleUpdate(submission.getId(),accessionedSamples);

        return certs;
    }

    protected void announceSampleUpdate(String submissionId, List<Sample> updatedSamples){

        if (updatedSamples.isEmpty()) return;

        UpdatedSamplesEnvelope updatedSamplesEnvelope = new UpdatedSamplesEnvelope();
        updatedSamplesEnvelope.setSubmissionId(submissionId);
        updatedSamplesEnvelope.setUpdatedSamples(updatedSamples);

        logger.info("sending sample update for submission {} with {} samples",submissionId,updatedSamples.size());

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
