package com.rangein.detective.ai_detective.dtos.controllers;

import com.rangein.detective.ai_detective.enums.Sex;

public record UserResponseDto (
        String  userId,
        String  gameId,

        String  username,
        Sex     sex,
        Integer age
) {}
