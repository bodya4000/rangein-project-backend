package com.rangein.detective.ai_detective.dtos.game;

public record SendMessageRequestDto(
        String gameId,
        String message
) {
}
