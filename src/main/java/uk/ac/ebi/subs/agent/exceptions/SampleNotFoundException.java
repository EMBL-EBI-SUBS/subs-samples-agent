package uk.ac.ebi.subs.agent.exceptions;

public class SampleNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7380626029205082044L;

    public SampleNotFoundException(String accession, Throwable e) {
        super("Could not find sample with accession: " + accession, e);
    }

}