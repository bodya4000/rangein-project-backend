package com.rangein.detective.ai_detective.dtos.services.ai;

import java.util.List;

public record AiSetupStoryResponseDto(
        String title,
        List<String> parts,
        List<String> options
        ) { }
