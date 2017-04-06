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

    public List<ProcessingCertificate> generateCertificates(List<Sample> sampleList) {
        logger.info("Generating certificates...");
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        sampleList.forEach(sample -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    sample,
                    Archive.BioSamples,
                    ProcessingStatusEnum.Accepted, // FIXME - infer correct status
                    sample.getAccession()
            );
            processingCertificateList.add(pc);
        });
        return processingCertificateList;
    }
}
