package uk.ac.ebi.subs.agent.converters;

import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Relationship;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UsiRelationshipToBsdRelationship {

    private Relationship convert(String sourceAccession, SampleRelationship usiRelationship) {
        Relationship bsdRelationship = null;
        if (usiRelationship != null) {
            bsdRelationship = Relationship.build(
                    sourceAccession,                            // source
                    usiRelationship.getRelationshipNature(),    // type
                    usiRelationship.getAccession()              // target
            );
        }
        return bsdRelationship;
    }

    public Set<Relationship> convert(Sample usiSample) {
        String sourceAccession = usiSample.getAccession();
        List<SampleRelationship> sampleRelationships = usiSample.getSampleRelationships();

        Set<Relationship> relationshipSet = new TreeSet<>();
        if (sampleRelationships == null) {
            return relationshipSet;
        }

        for (SampleRelationship usiRelationship : sampleRelationships) {
            if (usiRelationship.getAccession() != null) {
                //BioSamples understands relationships to accessioned samples, so you may need two passes to fully submit
                relationshipSet.add(convert(sourceAccession, usiRelationship));
            }
        }

        return relationshipSet;
    }

}
