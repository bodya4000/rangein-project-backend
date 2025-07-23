package com.rangein.detective.ai_detective.dtos.models.game.chars;

import java.util.List;

public record DbCharacter(
        String name,   // name
        String gender,   // gender ("m" / "f")
        List<String> messageColors
) { }

