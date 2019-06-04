package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.agent.services.FetchService;
import uk.ac.ebi.subs.agent.services.SubmissionService;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleExternalReference;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.processing.UpdatedSamplesEnvelope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This service is processing the submitted samples and insert or update them into the BioSamples database.
 */
@Component
public class SamplesProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SamplesProcessor.class);

    @Value("${biosamples.biostudies.core.url}")
    private String biostudiesCoreUrl;

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    private SubmissionService submissionService;

    private FetchService fetchService;

    private CertificatesGenerator certificatesGenerator;

    public SamplesProcessor(RabbitMessagingTemplate rabbitMessagingTemplate, SubmissionService submissionService,
                            FetchService fetchService,
                            CertificatesGenerator certificatesGenerator, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.submissionService = submissionService;
        this.fetchService = fetchService;
        this.certificatesGenerator = certificatesGenerator;

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

            List<Sample> samplesUpdated = submissionService.submit(samplesToUpdate, envelope.getJWTToken());
            logger.info("Updated {} samples for submission {}", samplesUpdated.size(), envelope.getSubmission().getId());
            announceSampleUpdate(submission.getId(), samplesUpdated);
            certificates.addAll(certificatesGenerator.generateCertificates(samplesUpdated));
        }

        // Create
        if (samplesWithUpdateRequirement.containsKey(false)) {
            List<Sample> samplesToCreate = samplesWithUpdateRequirement.get(false);

            List<Sample> samplesCreated = submissionService.submit(samplesToCreate, envelope.getJWTToken());
            logger.info("Created {} samples for submission {}", samplesCreated.size(), envelope.getSubmission().getId());
            certificates.addAll(certificatesGenerator.generateCertificates(samplesCreated));
        }

        // samples that need secondary update - you can't always know the accession of the referenced sample in the first round
        List<Sample> samplesInNeedOfSampleRelationshipAccessions =
                submittedSamplesInNeedOfSampleRelationshipAccessionUpdate(envelope);

        if (!samplesInNeedOfSampleRelationshipAccessions.isEmpty()) {
            updateSampleRelationshipAccessions(envelope, submission, certificates, samplesInNeedOfSampleRelationshipAccessions);
        }

        return certificates;
    }

    private void updateSampleRelationshipAccessions(SubmissionEnvelope envelope, Submission submission, List<ProcessingCertificate> certificates, List<Sample> samplesInNeedOfSampleRelationshipAccessions) {
        logger.info("Secondary update for sample relationship accessions for {} samples in {}",
                samplesInNeedOfSampleRelationshipAccessions.size(),
                submission.getId());

        Map<String,String> uuidToAccession = certificates.stream().collect(
                Collectors.toMap(ProcessingCertificate::getSubmittableId,ProcessingCertificate::getAccession)
        );
        Map<String,String> aliasToAccession = new HashMap<>();
        for (Sample s : envelope.getSamples()){
            String accession = uuidToAccession.get(s.getId());
            String alias = s.getAlias();
            aliasToAccession.put(alias,accession);
        }

        //add the accessions to the samples so updates work
        samplesInNeedOfSampleRelationshipAccessions.forEach(s ->
                s.setAccession( uuidToAccession.get( s.getId()) )
        );

        //add the accession for the referenced sample into the sample relationships
        samplesInNeedOfSampleRelationshipAccessions.stream()
                .flatMap(s -> s.getSampleRelationships().stream())
                .filter(sr -> sr.getAccession() == null)
                .forEach(sr -> sr.setAccession(aliasToAccession.get(sr.getAlias())));

        //update the samples with the accessions, don't worry about the certificates as we already have what we need
        submissionService.submit(samplesInNeedOfSampleRelationshipAccessions, envelope.getJWTToken());
    }

    private List<Sample> submittedSamplesInNeedOfSampleRelationshipAccessionUpdate(SubmissionEnvelope envelope) {
        return envelope.getSamples().stream()
                .filter(s -> s.getSampleRelationships() != null)
                .filter(s -> !s.getSampleRelationships().isEmpty())
                .filter(this::sampleHasSampleRelationshipsWithoutAccession)
                .collect(Collectors.toList());
    }

    private boolean sampleHasSampleRelationshipsWithoutAccession(Sample s) {
        return s.getSampleRelationships().stream()
                .anyMatch(sr -> sr.getAccession() == null);
    }

    protected List<Sample> findSamples(SubmissionEnvelope envelope) {
        logger.debug("Finding {} samples from {} submission", envelope.getSupportingSamplesRequired().size(), envelope.getSubmission().getId());

        List<String> accessions = new ArrayList<>();

        envelope.getSupportingSamplesRequired().forEach(sampleRef -> accessions.add(sampleRef.getAccession()));

        return fetchService.findSamples(accessions, envelope.getJWTToken());
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

    //update samples with BiostudiesId as an external reference, if JWT is not present we will use super user privileges
    protected List<Sample> updateAccessions(List<String> accessions, String biostudiesId, String jwt) {
        List<Sample> samples = fetchService.findSamples(accessions, jwt);
        logger.info("Update sample accessions: out of {} accessions, found {} samples", accessions.size(), samples.size());

        for (Sample sample : samples) {
            SampleExternalReference externalReference = new SampleExternalReference();
            externalReference.setUrl(biostudiesCoreUrl + biostudiesId);
            sample.addSampleExternalReference(externalReference);
        }

        return submissionService.submit(samples, jwt);
    }
}
