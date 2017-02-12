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
        String url = null;
        if(usiAttribute.getTerms() != null && !usiAttribute.getTerms().isEmpty()) {
            url = usiAttribute.getTerms().get(0).getUrl();  // Losing info !!
        }

        Attribute bsdAttribute = Attribute.build(
                usiAttribute.getName(),     // key
                usiAttribute.getValue(),    // value
                url,                        // iri
                usiAttribute.getUnits()     // unit
        );

        return bsdAttribute;
    }

    public Set<Attribute> convert(List<uk.ac.ebi.subs.data.component.Attribute> usiAttributes) {
        Set<Attribute> attributeSet = new TreeSet<>();
        if(usiAttributes != null) {
            for (uk.ac.ebi.subs.data.component.Attribute usiAttribute : usiAttributes) {
                attributeSet.add(convert(usiAttribute));
            }
        }
        return attributeSet;
    }
}
