package com.rangein.detective.ai_detective.dtos.controllers;

import com.rangein.detective.ai_detective.dtos.services.DetectiveStoryDto;

import java.util.List;

public record GameResponseDto(
        String                         gameId,
        DetectiveStoryDto              story,
        List<String>                   options
) {
}
