package com.rangein.detective.ai_detective.dtos.user;

import com.rangein.detective.ai_detective.enums.Sex;

public record UserInfoResponseDto (
        String userId,
        String gameId,

        String username,
        Sex sex,
        Integer age

) { }
