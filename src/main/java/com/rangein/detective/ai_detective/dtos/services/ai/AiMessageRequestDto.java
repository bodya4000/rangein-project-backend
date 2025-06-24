package com.rangein.detective.ai_detective.dtos.services.ai;

import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.enums.GameRole;

import java.util.List;

public record AiMessageRequestDto(
        List<MessageDto> messages,
        MessageDto       userMessage
) {
}
