package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;
import uk.ac.ebi.biosamples.model.ExternalReference;
import uk.ac.ebi.biosamples.model.Sample;
import uk.ac.ebi.biosamples.model.SubmittedViaType;
import uk.ac.ebi.subs.data.component.SampleExternalReference;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static uk.ac.ebi.subs.agent.utils.ConverterHelper.getInstantFromString;

/**
 * This is a converter class to convert USI's {@link uk.ac.ebi.subs.data.submittable.Sample} model
 * to BioSamples's {@link uk.ac.ebi.biosamples.model.Sample} model.
 */
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

        Instant updateDate = null;

        TreeSet<ExternalReference> externalRefs = new TreeSet<>();

        if(usiSample.getAttributes() != null) {

            for (Map.Entry<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> attributeEntry : usiSample.getAttributes().entrySet()) {
                if("update".equals(attributeEntry.getKey().toLowerCase())) {
                    updateDate = getInstantFromString(attributeEntry.getValue().iterator().next().getValue());
                }
            }

            Map<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> filteredAttributes = getOtherAttributes(usiSample.getAttributes());
            attributeSet = toBsdAttribute.convert(filteredAttributes);

        } else {
            attributeSet = new TreeSet<>();
        }

        if(usiSample.getTitle() != null) {
            Attribute att = Attribute.build("title", usiSample.getTitle());
            attributeSet.add(att);
        }

        Long taxonId = usiSample.getTaxonId();
        String taxon = usiSample.getTaxon();
        String uri = "";
        if (taxonId != null) {
            uri = ncbiBaseUrl + taxonId;
        }
        if (taxon != null) {
            attributeSet.add(Attribute.build("organism", taxon, uri, null));
        } else if (taxonId != null) {
            attributeSet.add(Attribute.build("organism", "", uri, null));
        }

        if(usiSample.getDescription() != null) {
            Attribute att = Attribute.build("description", usiSample.getDescription());
            attributeSet.add(att);
        }

        if (usiSample.getSampleExternalReferences() != null && !usiSample.getSampleExternalReferences().isEmpty()) {
            for (SampleExternalReference ref : usiSample.getSampleExternalReferences()) {
                externalRefs.add(ExternalReference.build(ref.getUrl()));
            }
        }

        Sample bioSample = Sample.build(
                usiSample.getAlias(),
                usiSample.getAccession(),
                usiSample.getTeam().getName(),
                usiSample.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                updateDate,
                attributeSet,
                toBsdRelationship.convert(usiSample),
                externalRefs,
                SubmittedViaType.USI);

        return bioSample;
    }

    private Map<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> getOtherAttributes(Map<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> attributes) {
        Map<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> filteredAttributes = new HashMap<>();
        filteredAttributes.putAll(attributes);
        filteredAttributes.remove("release");
        filteredAttributes.remove("update");
        return filteredAttributes;
    }
}
