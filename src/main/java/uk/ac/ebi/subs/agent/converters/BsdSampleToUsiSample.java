package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        attributes
                .parallelStream()
                .forEach(attribute -> {
            if("description".equals(attribute.getName())) {
                usiSample.setDescription(attribute.getValue());
            } else if("title".equals(attribute.getName())) {
                usiSample.setTitle(attribute.getValue());
            } else if("taxon".equals(attribute.getName())) {
                usiSample.setTaxon(attribute.getValue());
                String url = attribute.getTerms().get(0).getUrl();
                String taxon = url.substring(url.lastIndexOf("_") + 1).trim();
                usiSample.setTaxonId(Long.parseLong(taxon));
            }
        });

        List<Attribute> filteredAttributes = attributes
                .stream()
                .filter(attribute ->
                        !"description".equals(attribute.getName()) &&
                        !"title".equals(attribute.getName()) &&
                        !"taxon".equals(attribute.getName())
        ).collect(Collectors.toList());

        if(bioSample.getRelease() != null) {    // Release date
            Attribute release = new Attribute();
            release.setName("release");
            release.setValue(bioSample.getRelease().toString());
            filteredAttributes.add(release);
        }
        if(bioSample.getUpdate() != null) { // Update date
            Attribute update = new Attribute();
            update.setName("update");
            update.setValue(bioSample.getUpdate().toString());
            filteredAttributes.add(update);
        }
        usiSample.setAttributes(filteredAttributes);

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
