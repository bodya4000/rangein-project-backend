package com.rangein.detective.ai_detective.dtos.models.game;

import com.rangein.detective.ai_detective.dtos.models.game.chars.AiCharacter;

import java.util.List;

public record GameState(
        GameResult result,
        List<String> events,
        Integer      turn,     // turn
        String       scene,    // scene,

        List<AiCharacter> chars, // characters
        List<String> inventary,      // inventary

        List<String> objectiveness,      // objectives
        List<String> locations,      // locations
        List<String> flags,
        String culprit         // culprit
) { }
