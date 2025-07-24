package com.rangein.detective.ai_detective.dtos.models.game;

import com.rangein.detective.ai_detective.dtos.models.game.messages.AiCharacterMessage;

import java.util.List;


public record GameUpdate(
        List<AiCharacterMessage> m, // chars responses
        List<String> o,           // options
        GameState g               // game state
) { }
