package com.rangein.detective.ai_detective.dtos.models.game.chars;

import java.util.Map;

public record AiCharacter(

        String name,   // name
        String gender,   // gender ("m" / "f")
        String opinion,
        String opinionLong,
        Map<String, String> relations
) { }

