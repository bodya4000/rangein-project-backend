package com.rangein.detective.ai_detective.dtos.models.game;

import com.rangein.detective.ai_detective.dtos.models.game.chars.AiCharacter;

import java.util.List;
import java.util.Map;

public record GameState(

        GameResult result,
        List<String> events,
        Integer      turn,     // turn
        String       scene,    // scene,

        List<AiCharacter> npcs, // characters
        List<String> reasoning,      // inventary
        Map<String, Integer> accusationWeight,      // inventary

        List<String> objectiveness,      // objectives
        List<String> locations,      // locations
        List<String> flags,
        String mafia         // culprit
) { }
