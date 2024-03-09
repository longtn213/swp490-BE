package com.fpt.ssds.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class JsonSupport {

    private static final Logger log = LoggerFactory.getLogger(JsonSupport.class);

    public static final JavaTimeModule DEFAULT_TIME_MODULE = new JavaTimeModule();
    // order Module
    private static final HashMap<Module, ObjectMapper> mapperMap = new HashMap<>();

    static {
        mapperMap.put(DEFAULT_TIME_MODULE, new ObjectMapper().registerModule(DEFAULT_TIME_MODULE));
    }

    public static <T> T toObject(String fromValue, Class<T> clazz, Module module) {
        try {
            ObjectMapper mapper = getMapper(module);
            return mapper.readValue(fromValue, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T toObject(Object fromValue, Class<T> clazz) {
        return toObject(fromValue, clazz, DEFAULT_TIME_MODULE);
    }

    public static <T> T toObject(Object fromValue, Class<T> clazz, Module module) {
        try {
            ObjectMapper mapper = getMapper(module);
            if (fromValue instanceof String) {
                JsonNode dataJson = mapper.readTree(String.valueOf(fromValue));
                return mapper.convertValue(dataJson, clazz);
            } else {
                return mapper.convertValue(fromValue, clazz);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String toJson(Object data) {
        return toJson(data, DEFAULT_TIME_MODULE);
    }

    public static String toJson(Object data, Module module) {
        try {
            ObjectMapper mapper = getMapper(module);
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static ObjectMapper getMapper(Module module) {
        ObjectMapper mapper = mapperMap.get(module);
        if (mapper == null) {
            mapper = new ObjectMapper().registerModule(module);
        }
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
