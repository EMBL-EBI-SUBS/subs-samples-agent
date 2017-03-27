package uk.ac.ebi.subs.samplesrepo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.List;

public interface SampleRepository extends MongoRepository<Sample, String> {

    Sample findByAccession(String accession);

    @Query(value="{ 'team.name' : ?0, 'alias' : { $in : ?1 } }")
    List<Sample> findByTeamAndAlias(@Param(value="teamName") String teamName, @Param(value="alias") String[] alias);

}
