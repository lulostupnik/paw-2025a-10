package ar.edu.itba.paw.webapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {
        private static final ObjectMapper mapper = new ObjectMapper();
    private JsonUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
        public static String toJson(Object obj) {
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error serializando a JSON", e);
            }
        }
    }

