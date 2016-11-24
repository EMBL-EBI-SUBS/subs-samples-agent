package uk.ac.ebi.subs.agent.biosamples;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.*;

public class CharacteristicsDeserializer extends JsonDeserializer<Map<String, List<String>>> {

    @Override
    public Map<String, List<String>> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Map<String, List<String>> map = new HashMap<>();

        JsonNode node = parser.getCodec().readTree(parser);
        Iterator jsonNodeIterator = node.fieldNames();

        while (jsonNodeIterator.hasNext()) {
            List<String> stringList = new ArrayList<>();

            String characteristic = jsonNodeIterator.next().toString();
            ArrayNode an = (ArrayNode) node.get(characteristic);

            Iterator arrayNodeIterator = an.iterator();
            while (arrayNodeIterator.hasNext()) {
                ObjectNode on = (ObjectNode) arrayNodeIterator.next();

                on.forEach(o -> {
                    if (o instanceof TextNode) {
                        stringList.add(o.asText());
                    } else if (o instanceof ArrayNode) {
                        o.iterator().forEachRemaining(a -> stringList.add(a.asText()));
                    }
                });
            }

            map.put(characteristic, stringList);
        }

        return map;
    }
}
