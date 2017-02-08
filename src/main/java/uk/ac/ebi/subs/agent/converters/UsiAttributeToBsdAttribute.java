package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.models.Attribute;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UsiAttributeToBsdAttribute implements Converter<uk.ac.ebi.subs.data.component.Attribute, Attribute> {

    @Override
    public Attribute convert(uk.ac.ebi.subs.data.component.Attribute usiAttribute) {
        Attribute bsdAttribute = Attribute.build(
                usiAttribute.getName(),
                usiAttribute.getValue(),
                usiAttribute.getTerms().get(0).getUrl(), // Losing info !!
                usiAttribute.getUnits()
        );

        return bsdAttribute;
    }

    public Set<Attribute> convert(List<uk.ac.ebi.subs.data.component.Attribute> usiAttributes) {
        Set<Attribute> attributeSet = new TreeSet<>();

        usiAttributes.forEach(usiAattribute -> attributeSet.add(convert(usiAattribute)));
        return attributeSet;
    }
}
