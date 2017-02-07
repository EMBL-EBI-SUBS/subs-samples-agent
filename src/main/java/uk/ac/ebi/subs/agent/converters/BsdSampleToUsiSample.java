package uk.ac.ebi.subs.agent.converters;

import org.springframework.beans.factory.annotation.*;
import org.springframework.core.convert.converter.*;
import org.springframework.stereotype.*;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.Sample;

import java.util.*;

@Service
public class BsdSampleToUsiSample implements Converter<uk.ac.ebi.biosamples.models.Sample, Sample> {

    @Autowired
    BsdAttributeToUsiAttribute bsdAttributeToUsiAttribute;

    @Override
    public Sample convert(uk.ac.ebi.biosamples.models.Sample biosample) {
        Sample usiSample = new Sample();
        usiSample.setAccession(biosample.getAccession());
        usiSample.setArchive(Archive.BioSamples);
        usiSample.setAttributes(bsdAttributeToUsiAttribute.convert(biosample.getAttributes()));

        // TODO - continue

        return usiSample;
    }

    public List<Sample> convert(List<uk.ac.ebi.biosamples.models.Sample> biosamples) {
        List<Sample> usisamples = new ArrayList<>();

        biosamples.forEach(biosample -> usisamples.add(convert(biosample)));

        return usisamples;
    }
}
