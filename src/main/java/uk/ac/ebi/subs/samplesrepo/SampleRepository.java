package uk.ac.ebi.subs.samplesrepo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.subs.data.submittable.Sample;

public interface SampleRepository extends MongoRepository<Sample, String> {

    Sample findByAccession(String accession);

    @Query(value="{ 'domain.name' : ?0, 'alias' : ?1 }")
    Sample findByDomainAndAlias(@Param(value="domainName") String domainName, @Param(value="alias") String alias);

}
