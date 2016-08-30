package uk.ac.ebi.subs.samplesrepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.List;

@Service
public class SampleServiceImp  implements SampleService{

    private SampleRepository sampleRepository;

    @Autowired
    public void setSampleRepository(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @Override
    public List<Sample> listAllSamples() {
        return sampleRepository.findAll();
    }

    @Override
    public Sample getSampleById(String id) {
        return sampleRepository.findOne(id);
    }

    @Override
    public void saveSamples(List<Sample> samples) {
        sampleRepository.save(samples);
    }
}
