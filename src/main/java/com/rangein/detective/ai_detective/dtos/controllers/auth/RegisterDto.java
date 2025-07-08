package com.rangein.detective.ai_detective.dtos.controllers.auth;


import com.rangein.detective.ai_detective.enums.Sex;

public record RegisterDto (
        String  deviceId,
        String  username,
        Sex sex,
        Integer age
) {}
