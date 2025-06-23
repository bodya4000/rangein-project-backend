package com.rangein.detective.ai_detective.dtos.game;

import java.util.List;

public record AiSetupResponse(
        String title,
        List<String> parts,
        List<String> options
        ) { }
