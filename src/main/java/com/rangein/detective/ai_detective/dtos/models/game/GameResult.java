package com.rangein.detective.ai_detective.dtos.models.game;

import java.util.List;

public record GameResult(
        Boolean f,
        Boolean ok,
        String  m,

        List<String> dead
) {

}
