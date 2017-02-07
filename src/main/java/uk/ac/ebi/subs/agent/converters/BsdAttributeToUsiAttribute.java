package uk.ac.ebi.subs.agent.converters;


import org.springframework.core.convert.converter.*;
import org.springframework.stereotype.*;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.component.Attribute;

import java.util.*;

@Service
public class BsdAttributeToUsiAttribute implements Converter<uk.ac.ebi.biosamples.models.Attribute, Attribute> {

    @Override
    public Attribute convert(uk.ac.ebi.biosamples.models.Attribute bsdAttribute) {
        Attribute usiAttribute = new Attribute();
        usiAttribute.setName(bsdAttribute.getKey());
        usiAttribute.setValue(bsdAttribute.getValue());
        usiAttribute.setUnits(bsdAttribute.getUnit());

        Term term = new Term();
        term.setUrl(bsdAttribute.getIri());
        usiAttribute.setTerms(Arrays.asList(term));

        return usiAttribute;
    }

    public List<Attribute> convert(Set<uk.ac.ebi.biosamples.models.Attribute> bsdAttributes) {
        List<Attribute> usiAttributes = new ArrayList<>();

        bsdAttributes.forEach(bsdAttribute -> usiAttributes.add(convert(bsdAttribute)));

        return usiAttributes;
    }
}
