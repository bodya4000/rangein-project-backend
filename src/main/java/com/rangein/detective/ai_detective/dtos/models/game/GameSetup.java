package com.rangein.detective.ai_detective.dtos.models.game;

import com.rangein.detective.ai_detective.dtos.models.game.messages.AiCharacterMessage;

import java.util.List;

public record GameSetup(
        String       t,     // title
        List<String> p,     // parts
        List<String> o,     // options
        List<AiCharacterMessage> m, // init chars messages
        GameState g      // game state
) { }


