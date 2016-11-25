package uk.ac.ebi.subs.agent.biosamples;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

public class Sample {

    // AbstractSubsEntity
    private String type; // what's this?
    private String accession;
    private String alias;
    private String status;
    private String domain = "unknown";
    private String archive;
    @JsonProperty("name")
    private String title;
    private String description;
    @JsonProperty("characteristics")
    @JsonDeserialize(using = CharacteristicsDeserializer.class)
    private List<Attribute> attributes = new ArrayList<>();

    // Sample
    private String id;
    // sample relationships ???
    private Long taxonId;
    private String taxon;

    // BioSamples
    private String updateDate;
    private String releaseDate;
    /*
    private List<String> externalReferencesNames;
    private String contact;
    private String organization;
    private String publications;
    */

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(Long taxonId) {
        this.taxonId = taxonId;
    }

    public String getTaxon() {
        return taxon;
    }

    public void setTaxon(String taxon) {
        this.taxon = taxon;
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


    @Override
    public String toString() {
        return "Sample{" +
                "type='" + type + '\'' +
                ", accession='" + accession + '\'' +
                ", alias='" + alias + '\'' +
                ", status='" + status + '\'' +
                ", domain='" + domain + '\'' +
                ", archive='" + archive + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", attributes=" + attributes +
                ", id='" + id + '\'' +
                ", taxonId=" + taxonId +
                ", taxon='" + taxon + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
