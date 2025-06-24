package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record GameSetupInfoDto(
        DetectiveStoryDto   story,
        String              assistantIntro,
        List<String>        options
) {
}
