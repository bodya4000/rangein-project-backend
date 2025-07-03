package com.rangein.detective.ai_detective.dtos.services;

import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;

public record AiMessageRequest(
        GameState        gameState,
        MessageDto       userMessage
) {
}
