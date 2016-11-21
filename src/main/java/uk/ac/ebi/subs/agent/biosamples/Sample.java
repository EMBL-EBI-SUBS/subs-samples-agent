package uk.ac.ebi.subs.agent.biosamples;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

public class Sample {

    private String accession;
    private String name;
    private String description;

    private String updateDate;
    private String releaseDate;

    @JsonDeserialize(using = CharacteristicsDeserializer.class)
    private Map<String, List<String>> characteristics;
    private List<String> externalReferencesNames;
    private String contact;
    private String organization;
    private String publications;

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Map<String, List<String>> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Map<String, List<String>> characteristics) {
        this.characteristics = characteristics;
    }

    public List<String> getExternalReferencesNames() {
        return externalReferencesNames;
    }

    public void setExternalReferencesNames(List<String> externalReferencesNames) {
        this.externalReferencesNames = externalReferencesNames;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPublications() {
        return publications;
    }

    public void setPublications(String publications) {
        this.publications = publications;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "accession='" + accession + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", characteristics=" + characteristics +
                ", externalReferencesNames=" + externalReferencesNames +
                ", contact='" + contact + '\'' +
                ", organization='" + organization + '\'' +
                ", publications='" + publications + '\'' +
                '}';
    }
}
