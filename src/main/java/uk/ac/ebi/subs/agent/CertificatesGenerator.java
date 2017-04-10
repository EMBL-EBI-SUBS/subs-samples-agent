package uk.ac.ebi.subs.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.util.ArrayList;
import java.util.List;

@Component
public class CertificatesGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CertificatesGenerator.class);

    public List<ProcessingCertificate> acknowledgeReception(List<Sample> sampleList) {
        logger.debug("Acknowledging submission reception");

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleList.forEach(sample -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatusEnum.Received
            );
            processingCertificateList.add(pc);
        });

        return processingCertificateList;
    }

    public List<ProcessingCertificate> generateCertificates(List<Sample> sampleList) {
        logger.debug("Generating certificates...");

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleList.forEach(sample -> {
            if (sample.getAccession() != null && !sample.getAccession().isEmpty()){
                ProcessingCertificate pc = new ProcessingCertificate(
                        sample,
                        Archive.BioSamples,
                        ProcessingStatusEnum.Completed,
                        sample.getAccession()
                );
                processingCertificateList.add(pc);
            } else {
                ProcessingCertificate pc = new ProcessingCertificate(
                        sample,
                        Archive.BioSamples,
                        ProcessingStatusEnum.Error
                );
                processingCertificateList.add(pc);
            }
        });

        return processingCertificateList;
    }

}
