package com.rangein.detective.ai_detective.dtos.game;

import com.rangein.detective.ai_detective.enums.GameRole;

public record AiMessageDto(
        GameRole role,
        String message
) {
}
