package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;
import uk.ac.ebi.subs.data.component.Term;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This is a converter class to convert USI's {@link uk.ac.ebi.subs.data.component.Attribute} model
 * to BioSamples's {@link Attribute} model.
 */
@Service
public class UsiAttributeToBsdAttribute implements Converter<Map.Entry<String, Collection<uk.ac.ebi.subs.data.component.Attribute>>, Attribute> {

    @Override
    public Attribute convert(Map.Entry<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> usiAttributeEntry) {
        uk.ac.ebi.subs.data.component.Attribute usiAttribute = usiAttributeEntry.getValue().iterator().next();

        Collection<String> iris = Collections.emptySet();
        if (usiAttribute.getTerms() != null) {
            iris = usiAttribute.getTerms().stream()
                    .map(Term::getUrl)
                    .filter(iri -> iri != null)
                    .collect(Collectors.toList());
        }

        Attribute bsdAttribute = Attribute.build(
                usiAttributeEntry.getKey(),     // key
                usiAttribute.getValue(),        // value
                iris,                           // iris
                usiAttribute.getUnits()         // unit
        );

        return bsdAttribute;
    }

    public Set<Attribute> convert(Map<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> usiAttributes) {
        Set<Attribute> attributeSet = new TreeSet<>();
        if(usiAttributes != null) {
            for (Map.Entry<String, Collection<uk.ac.ebi.subs.data.component.Attribute>> usiAttributeEntry : usiAttributes.entrySet()) {
                attributeSet.add(convert(usiAttributeEntry));
            }
        }
        return attributeSet;
    }

}