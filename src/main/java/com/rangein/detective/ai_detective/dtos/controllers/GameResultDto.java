package com.rangein.detective.ai_detective.dtos.controllers;

import com.rangein.detective.ai_detective.enums.GameResult;

public record GameResultDto(
        Boolean    finished,
        GameResult result
) {
}
