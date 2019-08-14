package uk.ac.ebi.subs.agent.utils;

import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;

public class SampleSubmissionResponse {
    private Sample sample;
    private String message;
    private ProcessingStatusEnum status;

    public SampleSubmissionResponse(Sample sample, String message, ProcessingStatusEnum status) {
        this.sample = sample;
        this.message = message;
        this.status = status;
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

    public void setStatus(ProcessingStatusEnum status) {
        this.status = status;
    }

    public ProcessingStatusEnum getStatus() {
        return status;
    }

}
