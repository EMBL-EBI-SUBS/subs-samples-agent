package uk.ac.ebi.subs.agent.biosamples;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CharacteristicsDeserializer extends JsonDeserializer< List<Attribute>> {
    private static final Logger logger = LoggerFactory.getLogger(CharacteristicsDeserializer.class);

    @Override
    public List<Attribute> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        List<Attribute> attributes = new ArrayList<>();
        Attribute attribute;

        JsonNode node = parser.getCodec().readTree(parser);
        Iterator jsonNodeIterator = node.fieldNames();

        while (jsonNodeIterator.hasNext()) { // Characteristics iterator
            String characteristic = jsonNodeIterator.next().toString();
            ArrayNode an = (ArrayNode) node.get(characteristic);

            Iterator arrayNodeIterator = an.iterator();
            while (arrayNodeIterator.hasNext()) { // Unwind multiple characteristics entries

                attribute = new Attribute();
                attribute.setName(characteristic);

                ObjectNode on = (ObjectNode) arrayNodeIterator.next();
                Iterator fieldNameIterator = on.fieldNames();
                Iterator objectIterator = on.iterator();

                while (objectIterator.hasNext()) { // Extract characteristic structured content
                    String fieldName = fieldNameIterator.next().toString();
                    BaseJsonNode jsonNode = (BaseJsonNode) objectIterator.next();

                    switch (fieldName) {
                        case "text":
                            attribute.setValue(jsonNode.asText());
                            break;
                        case "unit":
                            attribute.setUnits(jsonNode.asText());
                            break;
                        case "ontologyTerms":
                            ArrayNode arrayNode = (ArrayNode) jsonNode;
                            List<String> ontoTerms = new ArrayList<>();
                            arrayNode.iterator().forEachRemaining(o -> ontoTerms.add(o.asText()));
                            attribute.setOntoTerms(ontoTerms);
                            break;
                        default:
                            logger.warn("Unknown BioSamples characteristic [" + fieldName + "] skipping it.");
                    }

                }

                attributes.add(attribute);
            }
        }

        return attributes;
    }
}
