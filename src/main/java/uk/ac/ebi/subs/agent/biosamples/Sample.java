package uk.ac.ebi.subs.agent.biosamples;

public class Sample {

    private String accession;
    private String name;
    private String description;

    private String updateDate;
    private String releaseDate;

    public Sample(String accession, String name, String description, String updateDate, String releaseDate) {
        this.accession = accession;
        this.name = name;
        this.description = description;
        this.updateDate = updateDate;
        this.releaseDate = releaseDate;
    }

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
}
