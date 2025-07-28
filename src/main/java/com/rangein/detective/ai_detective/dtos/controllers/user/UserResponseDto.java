package com.rangein.detective.ai_detective.dtos.controllers.user;


import java.util.List;

public record UserResponseDto (
        String  userId,
        String  gameId,

        String  username,
        Integer     sex,
        Integer age,
        List<String> avatarColors
) {}
