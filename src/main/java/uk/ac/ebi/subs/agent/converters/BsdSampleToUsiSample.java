package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BsdSampleToUsiSample implements Converter<uk.ac.ebi.biosamples.models.Sample, Sample> {

    @Autowired
    BsdAttributeToUsiAttribute toUsiAttribute;
    @Autowired
    BsdRelationshipToUsiRelationship toUsiRelationship;

    @Override
    public Sample convert(uk.ac.ebi.biosamples.models.Sample bioSample) {
        Sample usiSample = new Sample();
        usiSample.setAccession(bioSample.getAccession());
        usiSample.setArchive(Archive.BioSamples);
        usiSample.setAlias(bioSample.getName());

        List<Attribute> attributes = toUsiAttribute.convert(bioSample.getAttributes());
        if(bioSample.getRelease() != null) {    // Release date
            Attribute release = new Attribute();
            release.setName("release");
            release.setValue(bioSample.getRelease().toString());
            attributes.add(release);
        }
        Attribute update = new Attribute(); // Update date
        update.setName("update");
        update.setValue(LocalDateTime.now().toString());
        attributes.add(update);

        // TODO: Extract attributes from BioSample
        // - description
        // - title
        // - taxon
        // - taxon id
        // - domain

        usiSample.setAttributes(attributes);

        List<SampleRelationship> sampleRelationships = toUsiRelationship.convert(bioSample.getRelationships());
        usiSample.setSampleRelationships(sampleRelationships);

        return usiSample;
    }

    public List<Sample> convert(List<uk.ac.ebi.biosamples.models.Sample> biosamples) {
        List<Sample> usisamples = new ArrayList<>();

        biosamples.forEach(biosample -> usisamples.add(convert(biosample)));

        return usisamples;
    }
}
