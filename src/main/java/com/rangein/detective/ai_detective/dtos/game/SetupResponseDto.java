package com.rangein.detective.ai_detective.dtos.game;

import org.springframework.data.domain.Page;

import java.util.List;

public record SetupResponseDto(
        String gameId,
        DetectiveStoryDto story,
        Page<MessageDto> messages
) {
}
