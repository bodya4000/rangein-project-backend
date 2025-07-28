package com.rangein.detective.ai_detective.dtos.repositories;


public record MessageWithIdDto(
        String messageId,
        String sender,
        String message
) {
}
