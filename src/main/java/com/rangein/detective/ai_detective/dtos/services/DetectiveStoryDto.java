package com.rangein.detective.ai_detective.dtos.services;

import java.util.List;

public record DetectiveStoryDto(
    String title,
    List<String> parts
) {
}
