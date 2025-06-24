package com.rangein.detective.ai_detective.dtos.controllers;

import com.rangein.detective.ai_detective.enums.GameRole;

import java.util.List;

public record ReplyMessageDto
        (
                String       messageId,
                GameRole     role,
                String       message,
                List<String> options
        )
{}
