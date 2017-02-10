package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Relationship;
import uk.ac.ebi.subs.data.component.SampleRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

@Service
public class BsdRelationshipToUsiRelationship implements Converter<Relationship, SampleRelationship> {

    @Override
    public SampleRelationship convert(Relationship bsdRelationship) {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setAccession(bsdRelationship.getSource());
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
