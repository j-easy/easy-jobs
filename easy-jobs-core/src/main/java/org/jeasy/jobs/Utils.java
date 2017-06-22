package org.jeasy.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> parseParameters(String parameters) throws Exception {
        return objectMapper.readValue(parameters, HashMap.class);
    }
}
