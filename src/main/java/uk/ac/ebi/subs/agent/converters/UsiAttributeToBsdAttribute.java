package uk.ac.ebi.subs.agent.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;
import uk.ac.ebi.subs.data.component.Term;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class UsiAttributeToBsdAttribute implements Converter<uk.ac.ebi.subs.data.component.Attribute, Attribute> {

    private static final Logger logger = LoggerFactory.getLogger(UsiAttributeToBsdAttribute.class);

    @Override
    public Attribute convert(uk.ac.ebi.subs.data.component.Attribute usiAttribute) {

        Collection<String> termUrls = Collections.emptySet();


        if(usiAttribute.getTerms() != null) {
            termUrls = usiAttribute.getTerms().stream()
                    .map(Term::getUrl)
                    .filter(term -> term != null)
                    .collect(Collectors.toList());
        }

        Attribute bsdAttribute = Attribute.build(
                usiAttribute.getName(),     // key
                usiAttribute.getValue(),    // value
                termUrls,                   // iri
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
