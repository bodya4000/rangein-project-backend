package com.rangein.detective.ai_detective.dtos.services.ai;

import com.rangein.detective.ai_detective.dtos.models.game.GameState;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;

public record AiMessageRequest(
        GameState g,
        MessageDto       d
        ) {
}
