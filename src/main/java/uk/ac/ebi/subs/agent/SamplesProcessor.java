package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.agent.services.FetchService;
import uk.ac.ebi.subs.agent.services.IntegrityService;
import uk.ac.ebi.subs.agent.services.SubmissionService;
import uk.ac.ebi.subs.agent.services.UpdateService;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.processing.UpdatedSamplesEnvelope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SamplesProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SamplesProcessor.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private UpdateService updateService;
    @Autowired
    private FetchService fetchService;
    @Autowired
    private IntegrityService integrityService;

    @Autowired
    private CertificatesGenerator certificatesGenerator;

    @Autowired
    public SamplesProcessor(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    protected List<ProcessingCertificate> processSamples(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();

        logger.debug("Processing {} samples from submission {}", envelope.getSamples().size(), submission.getId());

        List<ProcessingCertificate> certificates = new ArrayList<>();

        // Set updateDate
        for (Sample sample : envelope.getSamples()) {
            Attribute attribute = new Attribute();
            attribute.setValue(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            sample.getAttributes().put("update", Arrays.asList(attribute));
        }

        envelope.getSamples().stream()
                .filter(s -> !s.isAccessioned())
                .forEach(
                        s -> integrityService.fillInSampleAccessionIfTeamAndAliasExistInBioSamples(s)
                );

        Map<Boolean, List<Sample>> samplesWithUpdateRequirement = envelope.getSamples().stream()
                .collect(
                        Collectors.groupingBy(
                                Sample::isAccessioned,
                                Collectors.toList()
                        )
                );

        // Update
        if (samplesWithUpdateRequirement.containsKey(true)) {
            List<Sample> samplesToUpdate = samplesWithUpdateRequirement.get(true);

            List<Sample> samplesUpdated = updateService.update(samplesToUpdate);
            logger.info("Updated {} samples for submission {}", samplesUpdated.size(), envelope.getSubmission().getId());
            announceSampleUpdate(submission.getId(), samplesUpdated);
            certificates.addAll(certificatesGenerator.generateCertificates(samplesUpdated));
        }

        // Create
        if (samplesWithUpdateRequirement.containsKey(false)) {
            List<Sample> samplesToCreate = samplesWithUpdateRequirement.get(false);

            List<Sample> samplesCreated = submissionService.submit(samplesToCreate);
            logger.info("Created {} samples for submission {}", samplesCreated.size(), envelope.getSubmission().getId());
            certificates.addAll(certificatesGenerator.generateCertificates(samplesCreated));
        }

        // samples that need secondary update
        List<Sample> samplesInNeedOfSampleRelationshipAccessions = envelope.getSamples().stream()
                .filter(s -> s.getSampleRelationships() != null)
                .filter(s -> !s.getSampleRelationships().isEmpty())
                .filter(s -> sampleHasSampleRelationshipsWithoutAccession(s))
                .collect(Collectors.toList());

        if (!samplesInNeedOfSampleRelationshipAccessions.isEmpty()) {
            logger.info("Secondary update for sample relationship accessions for {} samples in {}",
                    samplesInNeedOfSampleRelationshipAccessions.size(),
                    submission.getId());

            Map<String, String> aliasToAccession = envelope.getSamples().stream()
                    .collect(Collectors.toMap(Sample::getAlias, Sample::getAccession));

            samplesInNeedOfSampleRelationshipAccessions.stream()
                    .flatMap(s -> s.getSampleRelationships().stream())
                    .filter(sr -> sr.getAccession() == null)
                    .forEach(sr -> sr.setAccession(aliasToAccession.get(sr.getAlias())));

            List<Sample> samplesUpdated = updateService.update(samplesInNeedOfSampleRelationshipAccessions);
        }


        return certificates;
    }

    private boolean sampleHasSampleRelationshipsWithoutAccession(Sample s) {
        return s.getSampleRelationships().stream()
                .filter(sr -> sr.getAccession() == null)
                .findAny()
                .isPresent();
    }

    protected List<Sample> findSamples(SubmissionEnvelope envelope) {
        logger.debug("Finding {} samples from {} submission", envelope.getSupportingSamplesRequired().size(), envelope.getSubmission().getId());

        List<String> accessions = new ArrayList<>();

        envelope.getSupportingSamplesRequired().forEach(sampleRef -> accessions.add(sampleRef.getAccession()));

        return fetchService.findSamples(accessions);
    }

    private void announceSampleUpdate(String submissionId, List<Sample> updatedSamples) {
        if (!updatedSamples.isEmpty()) {
            UpdatedSamplesEnvelope updatedSamplesEnvelope = new UpdatedSamplesEnvelope();
            updatedSamplesEnvelope.setSubmissionId(submissionId);
            updatedSamplesEnvelope.setUpdatedSamples(updatedSamples);

            logger.debug("Submission {} with {} samples updates", submissionId, updatedSamples.size());

            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SAMPLES_UPDATED, updatedSamplesEnvelope);

        }
    }

}
