package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameState(
        GameResult res,
        List<String> events,
        Integer      turn,     // turn
        String       scene,    // scene,
        List<Character> chars, // characters
        List<String> inv,      // inventary

        List<String> obj,      // objectives
        List<String> loc,      // locations
        List<String> flags,
        String culprit         // culprit
) { }
