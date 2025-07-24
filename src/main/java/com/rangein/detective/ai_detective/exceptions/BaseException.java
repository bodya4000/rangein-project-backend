package com.rangein.detective.ai_detective.exceptions;

import com.google.gson.Gson;
import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private final int statusCode;
    private final String errorMessage;

    public BaseException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public String toJson() {
        return new Gson().toJson(new ErrorResponse(true, statusCode, errorMessage));
    }

    private record ErrorResponse(boolean error, int status, String message) {
    }
}