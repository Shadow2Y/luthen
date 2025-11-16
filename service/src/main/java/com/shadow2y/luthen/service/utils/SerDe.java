package com.shadow2y.luthen.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerDe {

    public static ObjectMapper mapper;

    public static void init(ObjectMapper objectMapper) {
        SerDe.mapper = objectMapper;
    }

    public static String writeValue(Object object) {
        if(object==null) return null;
        if(object instanceof String) return (String) object;
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String string, TypeReference<T> reference) {
        if(string==null) return null;
        try {
            return mapper.readValue(string, reference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
