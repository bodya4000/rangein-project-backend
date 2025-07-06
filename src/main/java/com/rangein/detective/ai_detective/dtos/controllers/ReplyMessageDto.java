package com.rangein.detective.ai_detective.dtos.controllers;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.enums.GameRole;

import java.util.List;

public record ReplyMessageDto
        (
                List<MessageWithIdDto> messages,
                List<String>    options,
                Boolean         finished,
                Integer         score
        )
{}
