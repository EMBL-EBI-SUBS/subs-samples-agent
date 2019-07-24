package uk.ac.ebi.subs.agent.utils;

import uk.ac.ebi.subs.data.submittable.Sample;

public class SampleSubmissionResponse {
    private Sample sample;
    private String message;

    public SampleSubmissionResponse(Sample sample, String message) {
        this.sample = sample;
        this.message = message;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
