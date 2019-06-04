package uk.ac.ebi.subs.agent.utils;

import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.model.ExternalReference;
import uk.ac.ebi.biosamples.model.Relationship;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component
public class TestUtils {

    // -- USI objects -- //

    public Team generateTeam (){
        Team team = new Team();
        team.setName("self.usi-team");
        return team;
    }

    public Sample generateUsiSample() {
        Sample usiSample = new Sample();
        usiSample.setAccession("SAM123");
        usiSample.setTeam(generateTeam());
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus.");
        usiSample.setAlias("This is an USI alias");
        usiSample.setReleaseDate(LocalDate.now());
        usiSample.setAttributes(generateUsiAttributes());
        usiSample.setSampleRelationships(Arrays.asList(generateUsiRelationship()));
        usiSample.setSampleExternalReferences(generateSampleExternalReferences());
        return usiSample;
    }

    public Sample generateUsiSampleForSubmission() {
        Sample usiSample = new Sample();
        usiSample.setTaxon("Mus musculus");
        usiSample.setTeam(generateTeam());
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus.");
        usiSample.setAlias("This is an USI alias");
        usiSample.setReleaseDate(LocalDate.of(2017, Month.AUGUST, 25));

        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();
        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        usiAttributes.put("age", Arrays.asList(usiAttribute_1));

        Attribute usiAttribute_2 = new Attribute();
        usiAttribute_2.setValue(Instant.now().toString());
        usiAttributes.put("update", Arrays.asList(usiAttribute_2));

        Attribute usiAttribute_3 = new Attribute();
        usiAttribute_3.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        usiAttribute_3.setTerms(Arrays.asList(t));
        usiAttributes.put("synonym", Arrays.asList(usiAttribute_3));

        usiSample.setAttributes(usiAttributes);

        usiSample.setSampleExternalReferences(generateSampleExternalReferences());

        return usiSample;
    }

    public Sample generateUsiSampleForUpdate() {
        Sample usiSample = new Sample();
        usiSample.setAccession("SAMEA100002");
        usiSample.setTeam(generateTeam());
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus - is this up to date?");
        usiSample.setAlias("This is an USI alias");
        usiSample.setReleaseDate(LocalDate.now());
        usiSample.setAttributes(generateUsiAttributes());
        usiSample.setSampleExternalReferences(generateSampleExternalReferences());
        return usiSample;
    }

    public Map<String, Collection<Attribute>> generateUsiAttributes() {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();

        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        usiAttributes.put("age", Arrays.asList(usiAttribute_1));

        Attribute usiAttribute_2 = new Attribute();
        usiAttribute_2.setValue(Instant.now().toString());
        usiAttributes.put("update", Arrays.asList(usiAttribute_2));

        Attribute usiAttribute_3 = new Attribute();
        usiAttribute_3.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        usiAttribute_3.setTerms(Arrays.asList(t));
        usiAttributes.put("synonym", Arrays.asList(usiAttribute_3));

        return usiAttributes;
    }

    public Map.Entry<String, Collection<Attribute>> generateUsiAttribute() {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();

        Attribute attribute = new Attribute();
        attribute.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        attribute.setTerms(Arrays.asList(term));
        attribute.setUnits("year");
        usiAttributes.put("age", Arrays.asList(attribute));

        return usiAttributes.entrySet().iterator().next();
    }

    public SampleRelationship generateUsiRelationship() {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setRelationshipNature("Child of");
        usiRelationship.setAccession("SAMEA104224787");
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
                        "This is a BioSamples name",
                        "SAM123",
                        "self.usi-team",
                        Instant.parse("2017-08-25T00:00:00Z"),
                        Instant.now(),
                        attributeSet,
                        relationshipSet,
                        new TreeSet<ExternalReference>()
        );
        return bsdSample;
    }

    public uk.ac.ebi.biosamples.model.Attribute generateBsdAttribute() {
        String uri = "http://purl.obolibrary.org/obo/UO_0000036";

        uk.ac.ebi.biosamples.model.Attribute bsdAttribute = uk.ac.ebi.biosamples.model.Attribute.build(
                "age",
                "1.5",
                uri,
                "year"
        );
        return bsdAttribute;
    }

    public Relationship generateBsdRelationship() {
        Relationship bsdRelationship = Relationship.build(
                "SAM123",
                "Child of",
                "SAM456"
        );
        return bsdRelationship;
    }

    public List<SampleExternalReference> generateSampleExternalReferences() {
        List<SampleExternalReference> externalReferences = new ArrayList<>();
        SampleExternalReference externalReference = new SampleExternalReference();
        externalReference.setUrl("https://www.ebi.ac.uk/biostudies/studies/S-EPMC5687625");
        externalReferences.add(externalReference);

        return externalReferences;
    }
}
