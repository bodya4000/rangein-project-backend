package com.rangein.detective.ai_detective.dtos.models.game;

import com.rangein.detective.ai_detective.dtos.models.game.chars.AiCharacter;

import java.util.List;

public record GameState(
        GameResult res,
        List<String> events,
        Integer      turn,     // turn
        String       scene,    // scene,
        List<AiCharacter> chars, // characters
        List<String> inv,      // inventary

        List<String> obj,      // objectives
        List<String> loc,      // locations
        List<String> flags,
        String culprit         // culprit
) { }
