package com.rangein.detective.ai_detective.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Sex {
    MALE(0),
    FEMALE(1);

    private final int code;

    Sex(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static Sex fromCode(int code) {
        for (Sex s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Invalid code for Sex: " + code);
    }
}
