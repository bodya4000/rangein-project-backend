package com.rangein.detective.ai_detective.dtos.controllers;


import java.util.List;

public record GameResponseDto(
        String                         gameId,
        String                         title,
        List<String>                   parts,
        List<String>                   options
) {
}
