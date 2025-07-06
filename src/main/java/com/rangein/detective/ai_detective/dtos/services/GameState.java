package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameState(
        Integer      tn,     // turn
        String       s,      // scene,
        List<Suspect> sus,    // suspects
        List<String> cl,     // cluse
        List<String> obj,    // objectives
        String       th,      // player theory
        String       cul,      // player theory
        List<Character> chars       // characters

) { }
