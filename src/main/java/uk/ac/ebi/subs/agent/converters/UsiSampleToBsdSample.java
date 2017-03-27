package uk.ac.ebi.subs.agent.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;
import uk.ac.ebi.biosamples.model.Sample;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@ConfigurationProperties()
public class UsiSampleToBsdSample implements Converter<uk.ac.ebi.subs.data.submittable.Sample, Sample> {

    @Autowired
    UsiAttributeToBsdAttribute toBsdAttribute;
    @Autowired
    UsiRelationshipToBsdRelationship toBsdRelationship;

    private String ncbiBaseUrl = "http://purl.obolibrary.org/obo/NCBITaxon_";

    private static final Logger logger = LoggerFactory.getLogger(UsiSampleToBsdSample.class);

    @Override
    public Sample convert(uk.ac.ebi.subs.data.submittable.Sample usiSample) {
        Set<Attribute> attributeSet;

        LocalDateTime release = null;
        LocalDateTime update = null;

        TreeSet<URI> externalRefs = new TreeSet<>();

        if(usiSample.getAttributes() != null) {
            for (uk.ac.ebi.subs.data.component.Attribute att : usiSample.getAttributes()) {
                if("release".equals(att.getName())) {
                    release = LocalDateTime.parse(att.getValue());
                }
                if("update".equals(att.getName())) {
                    update = LocalDateTime.parse(att.getValue());
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
            URI uri = null;
            try {
                URL url = new URL(ncbiBaseUrl + usiSample.getTaxonId());
                uri = url.toURI();
            } catch (MalformedURLException e) {
                logger.error("Malformed URL " + ncbiBaseUrl, e);
            } catch (URISyntaxException use) {
                logger.error("URISyntaxException " + ncbiBaseUrl, use);
            }

            Attribute att = Attribute.build("taxon", usiSample.getTaxon(), uri, null);
            attributeSet.add(att);
        }
        if(usiSample.getDescription() != null) {
            Attribute att = Attribute.build("description", usiSample.getDescription());
            attributeSet.add(att);
        }

        // Archive for samples is BioSamples

        Sample bioSample = Sample.build(
                usiSample.getAlias(),                                           // name
                usiSample.getAccession(),                                       // accession
                release,                                                        // release date
                update,                                                         // update date
                attributeSet,                                                   // attributes
                toBsdRelationship.convert(usiSample.getSampleRelationships()),  // relationships
                externalRefs
        );

        return bioSample;
    }

    public String getNcbiBaseUrl() {
        return ncbiBaseUrl;
    }

    public void setNcbiBaseUrl(String ncbiBaseUrl) {
        this.ncbiBaseUrl = ncbiBaseUrl;
    }
}
