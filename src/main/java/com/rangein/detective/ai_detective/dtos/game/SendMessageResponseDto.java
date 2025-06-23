package com.rangein.detective.ai_detective.dtos.game;

import com.rangein.detective.ai_detective.enums.GameRole;

import java.util.List;

public record SendMessageResponseDto(
        String messageId,
        GameRole role,
        String message,
        List<String> options
) {
}
