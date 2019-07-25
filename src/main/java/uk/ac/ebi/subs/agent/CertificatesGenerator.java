package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.agent.utils.SampleSubmissionResponse;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.util.ArrayList;
import java.util.List;

/**
 * This service is generates {@link ProcessingCertificate}s for a given list of submitted {@link Sample}s.
 */
@Component
public class CertificatesGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CertificatesGenerator.class);

    public List<ProcessingCertificate> generateCertificates(List<SampleSubmissionResponse> sampleResponseList) {
        logger.debug("Generating certificates...");

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleResponseList.forEach(sampleResponse -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    sampleResponse.getSample(),
                    Archive.BioSamples,
                    ProcessingStatusEnum.Completed,
                    sampleResponse.getSample().getAccession()
            );
            pc.setMessage(sampleResponse.getMessage());
            processingCertificateList.add(pc);
        });

        return processingCertificateList;
    }

}
