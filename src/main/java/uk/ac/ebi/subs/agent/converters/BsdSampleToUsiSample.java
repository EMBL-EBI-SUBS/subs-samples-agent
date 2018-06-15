package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BsdSampleToUsiSample implements Converter<uk.ac.ebi.biosamples.model.Sample, Sample> {

    @Autowired
    private BsdAttributeToUsiAttribute toUsiAttribute;
    @Autowired
    private BsdRelationshipToUsiRelationship toUsiRelationship;

    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String TAXON = "organism";

    @Override
    public Sample convert(uk.ac.ebi.biosamples.model.Sample bioSample) {
        Sample usiSample = new Sample();
        usiSample.setAccession(bioSample.getAccession());
        usiSample.setAlias(bioSample.getName());
        if (bioSample.getDomain() != null) {
            Team team = new Team();
            team.setName(bioSample.getDomain());
            usiSample.setTeam(team);
        }

        Map<String, Collection<Attribute>> attributes = toUsiAttribute.convert(bioSample.getAttributes());
        attributes.entrySet().iterator().forEachRemaining(attribute -> {
            if(DESCRIPTION.equals(attribute.getKey())) {
                usiSample.setDescription(attribute.getValue().iterator().next().getValue());
            } else if(TITLE.equals(attribute.getKey())) {
                usiSample.setTitle(attribute.getValue().iterator().next().getValue());
            } else if(TAXON.equals(attribute.getKey())) {
                usiSample.setTaxon(attribute.getValue().iterator().next().getValue());
                String url = attribute.getValue().iterator().next().getTerms().get(0).getUrl();
                String taxon = url.substring(url.lastIndexOf("_") + 1).trim();
                usiSample.setTaxonId(Long.parseLong(taxon));
            }
        });

        // Keep all non 'description', 'title' and 'taxon' attributes
        Map<String, Collection<Attribute>> filteredAttributes = getOtherAttributes(attributes);

        if(bioSample.getRelease() != null) {
            usiSample.setReleaseDate(
                    LocalDateTime.ofInstant(bioSample.getRelease(), ZoneOffset.UTC).toLocalDate());
        }
        if(bioSample.getUpdate() != null) {
            Attribute update = new Attribute();
            update.setValue(bioSample.getUpdate().toString());
            filteredAttributes.put("update", Arrays.asList(update));
        }
        usiSample.setAttributes(filteredAttributes);

        List<SampleRelationship> sampleRelationships = toUsiRelationship.convert(bioSample.getRelationships());
        usiSample.setSampleRelationships(sampleRelationships);

        return usiSample;
    }

    public List<Sample> convert(List<uk.ac.ebi.biosamples.model.Sample> biosamples) {
        List<Sample> usisamples = new ArrayList<>();

        biosamples.forEach(biosample -> usisamples.add(convert(biosample)));

        return usisamples;
    }

    private Map<String, Collection<Attribute>> getOtherAttributes(Map<String, Collection<Attribute>> attributes) {
        Map<String, Collection<Attribute>> filteredAttributes = new HashMap<>();
        filteredAttributes.putAll(attributes);
        filteredAttributes.remove(DESCRIPTION);
        filteredAttributes.remove(TITLE);
        filteredAttributes.remove(TAXON);
        return filteredAttributes;
    }
}
