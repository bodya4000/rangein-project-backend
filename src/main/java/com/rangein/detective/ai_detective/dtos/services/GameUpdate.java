package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameUpdate(
        String       m,     // message
        List<String> p,     // parts
        List<String> o,     // options
        GameState g,         // game state
        Boolean   f,
        Boolean  ok
) { }
