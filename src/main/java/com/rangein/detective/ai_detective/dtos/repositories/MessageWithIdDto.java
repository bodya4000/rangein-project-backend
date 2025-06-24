package com.rangein.detective.ai_detective.dtos.repositories;

import com.rangein.detective.ai_detective.enums.GameRole;

public record MessageWithIdDto(
        String messageId,
        GameRole role,
        String message
) {
}
