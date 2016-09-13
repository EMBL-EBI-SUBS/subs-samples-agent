package uk.ac.ebi.subs.samplesrepo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.subs.data.submittable.Sample;

public interface SampleRepository extends MongoRepository<Sample, String> {

    Sample findByAccession(String accession);
}
