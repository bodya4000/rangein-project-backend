package com.rangein.detective.ai_detective.dtos.game;

import org.springframework.data.domain.Page;


public record SetupResponseDto(
        String gameId,
        DetectiveStoryDto story,
        MessageData messageData
) {
}
