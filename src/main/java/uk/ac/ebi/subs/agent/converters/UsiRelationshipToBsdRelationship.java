package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Relationship;
import uk.ac.ebi.subs.data.component.SampleRelationship;

import java.util.List;
import java.util.Set;

@Service
public class UsiRelationshipToBsdRelationship implements Converter<SampleRelationship, Relationship> {

    @Override
    public Relationship convert(SampleRelationship sampleRelationship) {
        // TODO
        return null;
    }

    public Set<Relationship> convert(List<SampleRelationship> sampleRelationships) {
        // TODO
        return null;
    }
}
