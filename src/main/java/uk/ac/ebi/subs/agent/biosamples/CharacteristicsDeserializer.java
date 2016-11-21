package uk.ac.ebi.subs.agent.biosamples;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CharacteristicsDeserializer extends StdDeserializer<Map<String, List<String>>> {

    public CharacteristicsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<String, List<String>> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);


        return null;
    }
}
