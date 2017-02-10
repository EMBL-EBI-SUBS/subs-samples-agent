package uk.ac.ebi.subs.agent.utils;

import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.models.Relationship;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@Component
public class TestUtils {

    // -- USI objects -- //

    public Sample generateUsiSample() {
        Sample usiSample = new Sample();
        usiSample.setArchive(Archive.BioSamples);
        usiSample.setAccession("SAM123");
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus.");
        usiSample.setAlias("This is an USI alias");
        usiSample.setAttributes(Arrays.asList(
                generateUsiAttribute()
        ));
        usiSample.setSampleRelationships(Arrays.asList(
                generateUsiRelationship()
        ));
        return usiSample;
    }

    public Attribute generateUsiAttribute() {
        Attribute usiAttribute = new Attribute();
        usiAttribute.setName("age");
        usiAttribute.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute.setTerms(Arrays.asList(term));
        usiAttribute.setUnits("year");
        return usiAttribute;
    }

    public SampleRelationship generateUsiRelationship() {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setRelationshipNature("Child of");
        usiRelationship.setAccession("SAM123");
        return usiRelationship;
    }

    // -- BioSamples objects -- //

    public uk.ac.ebi.biosamples.models.Sample generateBsdSample() {
        Set<uk.ac.ebi.biosamples.models.Attribute> attributeSet = new TreeSet<>();
        attributeSet.add(generateBsdAttribute());

        Set<Relationship> relationshipSet = new TreeSet<>();
        relationshipSet.add(generateBsdRelationship());

        uk.ac.ebi.biosamples.models.
                Sample bsdSample = uk.ac.ebi.biosamples.models.Sample.build(
                        "This is a BioSamples name",    // name
                        "SAM123",                       // accession
                        LocalDateTime.now(),            // release date
                        LocalDateTime.now(),            // update date
                        attributeSet,                   // attributes
                        relationshipSet                 // relationships
        );

        return bsdSample;
    }

    public uk.ac.ebi.biosamples.models.Attribute generateBsdAttribute() {
        uk.ac.ebi.biosamples.models.Attribute bsdAttribute = uk.ac.ebi.biosamples.models.Attribute.build(
                "age",
                "1.5",
                "http://purl.obolibrary.org/obo/UO_0000036",
                "year"
        );
        return bsdAttribute;
    }

    public Relationship generateBsdRelationship() {
        Relationship bsdRelationship = Relationship.build(
                "Child of", // type
                "",         // target
                "SAM123"    // source
        );
        return bsdRelationship;
    }
}
