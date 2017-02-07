package uk.ac.ebi.subs.agent.services;

import org.springframework.stereotype.*;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.*;

@Service
public class SampleConverterService {

    public List<Sample> convertFromBiosampleToUsiSample(List<uk.ac.ebi.biosamples.models.Sample> biosamples) {
        return new ArrayList<>();
    }

    public List<uk.ac.ebi.biosamples.models.Sample> convertFromUsiSampleToBiosample(List<Sample> usiSamples) {
        return new ArrayList<>();
    }
}
