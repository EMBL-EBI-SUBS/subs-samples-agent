package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;
import uk.ac.ebi.biosamples.model.ExternalReference;
import uk.ac.ebi.biosamples.model.Sample;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static uk.ac.ebi.subs.agent.utils.ConverterHelper.getInstantFromString;

@Service
@ConfigurationProperties()
public class UsiSampleToBsdSample implements Converter<uk.ac.ebi.subs.data.submittable.Sample, Sample> {

    @Autowired
    private UsiAttributeToBsdAttribute toBsdAttribute;
    @Autowired
    private UsiRelationshipToBsdRelationship toBsdRelationship;

    private String ncbiBaseUrl = "http://purl.obolibrary.org/obo/NCBITaxon_";

    @Override
    public Sample convert(uk.ac.ebi.subs.data.submittable.Sample usiSample) {
        Set<Attribute> attributeSet;

        Instant releaseDate = null;
        Instant updateDate = null;

        TreeSet<ExternalReference> externalRefs = new TreeSet<>();

        if(usiSample.getAttributes() != null) {
            for (uk.ac.ebi.subs.data.component.Attribute att : usiSample.getAttributes()) {
                if("release".equals(att.getName().toLowerCase())) {
                    releaseDate = getInstantFromString(att.getValue());
                }
                if("update".equals(att.getName().toLowerCase())) {
                    updateDate = getInstantFromString(att.getValue());
                }
            }

            List<uk.ac.ebi.subs.data.component.Attribute> attributeList = new ArrayList<>(usiSample.getAttributes());
            attributeList.removeIf(attribute -> "release".equals(attribute.getName()) || "update".equals(attribute.getName()));
            attributeSet = toBsdAttribute.convert(attributeList);

        } else {
            attributeSet = new TreeSet<>();
        }

        if(usiSample.getTitle() != null) {
            Attribute att = Attribute.build("title", usiSample.getTitle());
            attributeSet.add(att);
        }
        if(usiSample.getTaxon() != null) {
            String uri = ncbiBaseUrl + usiSample.getTaxonId();
            Attribute att = Attribute.build("taxon", usiSample.getTaxon(), uri, null);
            attributeSet.add(att);
        }
        if(usiSample.getDescription() != null) {
            Attribute att = Attribute.build("description", usiSample.getDescription());
            attributeSet.add(att);
        }

        Sample bioSample = Sample.build(
                usiSample.getAlias(),
                usiSample.getAccession(),
                usiSample.getTeam().getName(),
                releaseDate,
                updateDate,
                attributeSet,
                toBsdRelationship.convert(usiSample),
                externalRefs
        );
        return bioSample;
    }
}
