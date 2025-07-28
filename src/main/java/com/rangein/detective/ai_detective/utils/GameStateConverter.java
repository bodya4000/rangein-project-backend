package com.rangein.detective.ai_detective.utils;

import com.google.gson.Gson;
import com.rangein.detective.ai_detective.dtos.models.game.GameState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.postgresql.util.PGobject;

@Converter(autoApply = false)
public class GameStateConverter implements AttributeConverter<GameState, Object> {

    private static final Gson gson = new Gson();

    @Override
    public Object convertToDatabaseColumn(GameState attribute) {
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(gson.toJson(attribute));
            return pgObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert GameState to PGobject", e);
        }
    }

    @Override
    public GameState convertToEntityAttribute(Object dbData) {
        if (dbData == null) return null;
        try {
            String json;
            if (dbData instanceof PGobject) {
                json = ((PGobject) dbData).getValue();
            } else {
                json = dbData.toString(); // fallback
            }
            return gson.fromJson(json, GameState.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert PGobject to GameState", e);
        }
    }
}


