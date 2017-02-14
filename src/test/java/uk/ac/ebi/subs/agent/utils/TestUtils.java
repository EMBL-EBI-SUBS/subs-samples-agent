package uk.ac.ebi.subs.agent.utils;

import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.model.Relationship;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        usiSample.setAttributes(
                generateUsiAttributes()
        );
        usiSample.setSampleRelationships(Arrays.asList(
                generateUsiRelationship()
        ));
        return usiSample;
    }

    public Sample generateUsiSampleForSubmission() {
        Sample usiSample = new Sample();
        usiSample.setArchive(Archive.BioSamples);
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus.");
        usiSample.setAlias("This is an USI alias");

        List<Attribute> attributeList = new ArrayList<>();
        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setName("age");
        usiAttribute_1.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        attributeList.add(usiAttribute_1);
        // 2
        Attribute usiAttribute_2 = new Attribute();
        usiAttribute_2.setName("release");
        usiAttribute_2.setValue(LocalDateTime.of(2016, Month.APRIL, 12, 12, 0, 0).toString());
        attributeList.add(usiAttribute_2);
        // 3
        Attribute usiAttribute_3 = new Attribute();
        usiAttribute_3.setName("update");
        usiAttribute_3.setValue(LocalDateTime.now().toString());
        attributeList.add(usiAttribute_3);
        // 4
        Attribute usiAttribute_4 = new Attribute();
        usiAttribute_4.setName("synonym");
        usiAttribute_4.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        usiAttribute_4.setTerms(Arrays.asList(t));
        attributeList.add(usiAttribute_4);
        usiSample.setAttributes(
                attributeList
        );

        return usiSample;
    }

    public Sample generateUsiSampleForUpdate() {
        Sample usiSample = new Sample();
        usiSample.setArchive(Archive.BioSamples);
        usiSample.setAccession("TSTE107");
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus - is this up to date?");
        usiSample.setAlias("This is an USI alias");
        usiSample.setAttributes(
                generateUsiAttributes()
        );
        return usiSample;
    }

    public List<Attribute> generateUsiAttributes() {
        List<Attribute> attributeList = new ArrayList<>();
        // 1
        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setName("age");
        usiAttribute_1.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        attributeList.add(usiAttribute_1);
        // 2
        Attribute usiAttribute_2 = new Attribute();
        usiAttribute_2.setName("release");
        usiAttribute_2.setValue(LocalDateTime.of(2016, Month.APRIL, 12, 12, 0, 0).toString());
        attributeList.add(usiAttribute_2);
        // 3
        Attribute usiAttribute_3 = new Attribute();
        usiAttribute_3.setName("update");
        usiAttribute_3.setValue(LocalDateTime.of(2016, Month.MAY, 12, 12, 0, 0).toString());
        attributeList.add(usiAttribute_3);
        // 4
        Attribute usiAttribute_4 = new Attribute();
        usiAttribute_4.setName("synonym");
        usiAttribute_4.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        usiAttribute_4.setTerms(Arrays.asList(t));
        attributeList.add(usiAttribute_4);

        return attributeList;
    }

    public Attribute generateUsiAttribute() {
        Attribute attribute = new Attribute();
        attribute.setName("age");
        attribute.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        attribute.setTerms(Arrays.asList(term));
        attribute.setUnits("year");
        return attribute;
    }

    public SampleRelationship generateUsiRelationship() {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setRelationshipNature("Child of");
        usiRelationship.setAccession("SAM123");
        return usiRelationship;
    }

    // -- BioSamples objects -- //

    public uk.ac.ebi.biosamples.model.Sample generateBsdSample() {
        Set<uk.ac.ebi.biosamples.model.Attribute> attributeSet = new TreeSet<>();
        attributeSet.add(generateBsdAttribute());

        Set<Relationship> relationshipSet = new TreeSet<>();
        relationshipSet.add(generateBsdRelationship());

        uk.ac.ebi.biosamples.model.
                Sample bsdSample = uk.ac.ebi.biosamples.model.Sample.build(
                        "This is a BioSamples name",    // name
                        "SAM123",                       // accession
                        LocalDateTime.now(),            // release date
                        LocalDateTime.now(),            // update date
                        attributeSet,                   // attributes
                        relationshipSet                 // relationships
        );

        return bsdSample;
    }

    public uk.ac.ebi.biosamples.model.Attribute generateBsdAttribute() {
        uk.ac.ebi.biosamples.model.Attribute bsdAttribute = uk.ac.ebi.biosamples.model.Attribute.build(
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
