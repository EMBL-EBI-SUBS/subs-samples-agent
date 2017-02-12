package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class BsdAttributeToUsiAttribute implements Converter<uk.ac.ebi.biosamples.models.Attribute, Attribute> {

    @Override
    public Attribute convert(uk.ac.ebi.biosamples.models.Attribute bsdAttribute) {
        Attribute usiAttribute = new Attribute();
        usiAttribute.setName(bsdAttribute.getKey());
        usiAttribute.setValue(bsdAttribute.getValue());
        usiAttribute.setUnits(bsdAttribute.getUnit());

        Term term = new Term();
        if(bsdAttribute.getIri() != null && !bsdAttribute.getIri().isEmpty()) {
            term.setUrl(bsdAttribute.getIri());
            usiAttribute.setTerms(Arrays.asList(term));
        }

        return usiAttribute;
    }

    public List<Attribute> convert(Set<uk.ac.ebi.biosamples.models.Attribute> bsdAttributes) {
        List<Attribute> usiAttributes = new ArrayList<>();

        bsdAttributes.forEach(bsdAttribute -> usiAttributes.add(convert(bsdAttribute)));

        return usiAttributes;
    }
}
