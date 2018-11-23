package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Relationship;
import uk.ac.ebi.subs.data.component.SampleRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * This is a converter class to convert BioSamples's {@link Relationship} model to USI {@link SampleRelationship} model.
 */
@Service
public class BsdRelationshipToUsiRelationship implements Converter<Relationship, SampleRelationship> {

    @Override
    public SampleRelationship convert(Relationship bsdRelationship) {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setAccession(bsdRelationship.getTarget());
        usiRelationship.setRelationshipNature(bsdRelationship.getType());
        return usiRelationship;
    }

    public List<SampleRelationship> convert(SortedSet<Relationship> bsdRelationships) {
        List<SampleRelationship> usiRelationships = new ArrayList<>();
        if(bsdRelationships != null) {
            for(Relationship relationship : bsdRelationships) {
                usiRelationships.add(convert(relationship));
            }
        }
        return usiRelationships;
    }
}
