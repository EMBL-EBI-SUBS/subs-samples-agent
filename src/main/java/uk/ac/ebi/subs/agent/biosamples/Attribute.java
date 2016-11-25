package uk.ac.ebi.subs.agent.biosamples;

import java.util.List;

public class Attribute {
    private String name;
    private String value;
    private String units;
    private List<String> ontoTerms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public List<String> getOntoTerms() {
        return ontoTerms;
    }

    public void setOntoTerms(List<String> ontoTerms) {
        this.ontoTerms = ontoTerms;
    }


    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", units='" + units + '\'' +
                ", ontoTerms=" + ontoTerms +
                '}';
    }
}
