package com.rangein.detective.ai_detective.dtos.repositories;

import com.rangein.detective.ai_detective.enums.GameRole;

public record MessageDto(
        GameRole role,
        String message
) {
}
