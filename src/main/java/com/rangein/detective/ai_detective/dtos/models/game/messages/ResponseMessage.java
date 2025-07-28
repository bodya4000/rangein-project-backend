package com.rangein.detective.ai_detective.dtos.models.game.messages;

import java.util.List;

public record ResponseMessage (
    String messageId,
    String sender,
    String message,
    List<String> colors
    ) {}
