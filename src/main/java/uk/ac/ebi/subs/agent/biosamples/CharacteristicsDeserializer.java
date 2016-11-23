package uk.ac.ebi.subs.agent.biosamples;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

public class CharacteristicsDeserializer extends JsonDeserializer<Map<String, List<String>>> {

    @Override
    public Map<String, List<String>> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Map<String, List<String>> map = new HashMap<>();
        List<String> stringList = new ArrayList<>();

        JsonNode node = parser.getCodec().readTree(parser);

        node.forEach(System.out::println);

        // Mock for now
        Map<String, List<String>> map2 = new HashMap<>();
        map2.put("mockString", Arrays.asList("list1", "list2"));

        return map2;
    }
}
