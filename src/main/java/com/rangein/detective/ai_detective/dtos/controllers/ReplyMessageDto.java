package com.rangein.detective.ai_detective.dtos.controllers;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;

import java.util.List;

public record ReplyMessageDto
        (
                List<MessageWithIdDto> messages,
                List<String>    options,
                Boolean         finished,
                Integer         score,
                String          summary
        )
{}
