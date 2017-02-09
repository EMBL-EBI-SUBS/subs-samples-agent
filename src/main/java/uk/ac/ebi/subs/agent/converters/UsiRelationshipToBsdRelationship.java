package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Relationship;
import uk.ac.ebi.subs.data.component.SampleRelationship;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UsiRelationshipToBsdRelationship implements Converter<SampleRelationship, Relationship> {

    @Override
    public Relationship convert(SampleRelationship usiRelationship) {
        Relationship bsdRelationship = null;
        if(usiRelationship != null) {
            bsdRelationship = Relationship.build(
                    usiRelationship.getRelationshipNature(),    // type
                    usiRelationship.getAccession(),             // target
                    null                                        // source ???
            );
        }
        return bsdRelationship;
    }

    public Set<Relationship> convert(List<SampleRelationship> sampleRelationships) {
        Set<Relationship> relationshipSet = new TreeSet<>();
        if(sampleRelationships != null) {
            for(SampleRelationship usiRelationship : sampleRelationships) {
                relationshipSet.add(convert(usiRelationship));
            }
        }
        return relationshipSet;
    }
}
