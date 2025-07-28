package com.rangein.detective.ai_detective.dtos.controllers;
import com.rangein.detective.ai_detective.dtos.models.game.messages.ResponseMessage;

import java.util.List;

public record ReplyMessageDto
        (
                List<ResponseMessage> messages,
                List<String>    options,
                List<String>    listOfDead,
                String          justBeenDead,
                Boolean         finished,
                Integer         score,
                String          summary
        )
{}
