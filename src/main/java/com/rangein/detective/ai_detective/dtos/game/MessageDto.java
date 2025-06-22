package com.rangein.detective.ai_detective.dtos.game;

import com.rangein.detective.ai_detective.enums.GameRole;

public record MessageDto(
        String messageId,
        GameRole role,
        String message
) {
}
