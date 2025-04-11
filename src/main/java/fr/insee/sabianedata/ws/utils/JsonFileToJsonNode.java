package fr.insee.sabianedata.ws.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public final class JsonFileToJsonNode {

    private JsonFileToJsonNode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static JsonNode getJsonNodeFromFile(File file) {
        JsonNode result;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readTree(file);
        } catch (IOException e) {
            log.error("Problem with file {}", file);
            log.error("Something went wrong when mapping to JsonNode", e);
            result = null;
        }
        return result;
    }
}
