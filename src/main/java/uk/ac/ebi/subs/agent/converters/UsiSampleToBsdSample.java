package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Attribute;
import uk.ac.ebi.biosamples.models.Sample;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UsiSampleToBsdSample implements Converter<uk.ac.ebi.subs.data.submittable.Sample, Sample> {

    @Autowired
    UsiAttributeToBsdAttribute toBsdAttribute;
    @Autowired
    UsiRelationshipToBsdRelationship toBsdRelationship;

    @Override
    public Sample convert(uk.ac.ebi.subs.data.submittable.Sample usiSample) {
        Set<Attribute> attributeSet;
        if (usiSample.getAttributes() != null) {
            attributeSet = toBsdAttribute.convert(usiSample.getAttributes());    // USI attributes to BioSd attributes
        } else {
            attributeSet = new TreeSet<>();
        }

        if (usiSample.getTaxon() != null) {
            Attribute att = Attribute.build("organism", usiSample.getTaxon());
            attributeSet.add(att);
        }
        if(usiSample.getTaxon() != null) {
            Attribute att = Attribute.build("taxonomic id", usiSample.getTaxonId().toString());
            attributeSet.add(att);
        }
        if(usiSample.getDescription() != null) {
            Attribute att = Attribute.build("description", usiSample.getDescription());
            attributeSet.add(att);
        }

        Sample bioSample = Sample.build(
                usiSample.getAlias(),                                           // name
                usiSample.getAccession(),                                       // accession
                LocalDateTime.now(),                                            // release date
                LocalDateTime.now(),                                            // update date
                attributeSet,                                                   // attributes
                toBsdRelationship.convert(usiSample.getSampleRelationships())   // relationships
        );

        return bioSample;
    }
}
