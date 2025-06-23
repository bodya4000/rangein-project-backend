package com.rangein.detective.ai_detective.dtos.game;

import java.util.List;

public record MessageData(
        List<MessageDto> messages,
        Integer pageNumber,
        Integer pageSize,
        Integer totalPages,
        Integer totalElements
) {
}

