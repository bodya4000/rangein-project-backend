package com.rangein.detective.ai_detective.exceptions.game;

import com.rangein.detective.ai_detective.exceptions.BaseException;

public class ValidateMessageException extends BaseException {

    public ValidateMessageException(String errorMessage) {
        super(400, errorMessage);
    }
}
