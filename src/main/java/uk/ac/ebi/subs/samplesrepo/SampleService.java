package uk.ac.ebi.subs.samplesrepo;

import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.List;

public interface SampleService {

    List<Sample> listAllSamples();

    Sample getSampleById(String id);

    void saveSamples(List<Sample> samples);
}
