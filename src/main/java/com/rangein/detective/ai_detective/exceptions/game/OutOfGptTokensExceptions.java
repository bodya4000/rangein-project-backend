package com.rangein.detective.ai_detective.exceptions.game;

import com.rangein.detective.ai_detective.exceptions.BaseException;

public class OutOfGptTokensExceptions extends BaseException {
    public OutOfGptTokensExceptions() {
        super(429,"Out of Gpt tokens. Received a 429 status.");
    }
}
