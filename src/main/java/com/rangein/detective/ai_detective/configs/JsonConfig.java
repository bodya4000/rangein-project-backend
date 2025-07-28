package com.rangein.detective.ai_detective.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.time.Instant;

@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new JsonSerializer<Instant>() {
                    @Override
                    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString()); // або src.toEpochMilli()
                    }
                })
                .create();
    }
}
