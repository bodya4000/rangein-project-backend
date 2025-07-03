package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameSetup(
        String       t,     // title
        List<String> p,     // parts
        List<String> o,     // options
        GameState    g      // game state
) { }
