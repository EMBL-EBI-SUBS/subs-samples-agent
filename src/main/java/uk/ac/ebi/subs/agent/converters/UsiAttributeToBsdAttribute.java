package uk.ac.ebi.subs.agent.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.model.Attribute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UsiAttributeToBsdAttribute implements Converter<uk.ac.ebi.subs.data.component.Attribute, Attribute> {

    private static final Logger logger = LoggerFactory.getLogger(UsiAttributeToBsdAttribute.class);

    @Override
    public Attribute convert(uk.ac.ebi.subs.data.component.Attribute usiAttribute) {
        String uri = null;
        if(usiAttribute.getTerms() != null && !usiAttribute.getTerms().isEmpty()) {
            uri = usiAttribute.getTerms().get(0).getUrl();

        }

        Attribute bsdAttribute = Attribute.build(
                usiAttribute.getName(),     // key
                usiAttribute.getValue(),    // value
                uri,                        // iri
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
