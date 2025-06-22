package com.rangein.detective.ai_detective.dtos.game;


public record GameSetupInfoDto (
    DetectiveStoryDto story,
    AiMessageDto introMessage
) {}
