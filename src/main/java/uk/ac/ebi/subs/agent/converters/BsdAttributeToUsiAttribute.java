package uk.ac.ebi.subs.agent.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BsdAttributeToUsiAttribute implements Converter<uk.ac.ebi.biosamples.model.Attribute, Map.Entry<String, Collection<uk.ac.ebi.subs.data.component.Attribute>>> {

    @Override
    public Map.Entry<String, Collection<Attribute>> convert(uk.ac.ebi.biosamples.model.Attribute bsdAttribute) {
        Attribute usiAttribute = new Attribute();
        usiAttribute.setValue(bsdAttribute.getValue());
        usiAttribute.setUnits(bsdAttribute.getUnit());

        if(bsdAttribute.getIri() != null) {
            List<Term> terms = bsdAttribute.getIri().stream()
                    .filter( bsdIri -> bsdIri != null)
                    .map(bsdIri -> makeTerm(bsdIri))
                    .collect(Collectors.toList());

            usiAttribute.setTerms(terms);
        }

        Map<String, Collection<Attribute>> map = new HashMap<>();
        map.put(bsdAttribute.getType(), Arrays.asList(usiAttribute));
        return map.entrySet().iterator().next();
    }

    public Map<String, Collection<Attribute>> convert(Set<uk.ac.ebi.biosamples.model.Attribute> bsdAttributes) {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();

        if (bsdAttributes != null && !bsdAttributes.isEmpty()) {
            for (uk.ac.ebi.biosamples.model.Attribute bsdAttribute : bsdAttributes) {
                Map.Entry<String, Collection<Attribute>> usiAttributeEntry = convert(bsdAttribute);
                usiAttributes.put(usiAttributeEntry.getKey(), usiAttributeEntry.getValue());
            }
        }

        return usiAttributes;
    }

    private Term makeTerm(String iri){
        Term t = new Term();
        t.setUrl(iri);
        return t;
    }
}
