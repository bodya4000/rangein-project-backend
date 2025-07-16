package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameUpdate(
        List<CharacterMessage> m, // chars responses
        List<String> o,           // options
        GameState g               // game state
) { }
