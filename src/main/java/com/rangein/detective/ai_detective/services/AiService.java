package com.rangein.detective.ai_detective.services;


import com.rangein.detective.ai_detective.dtos.services.DetectiveStoryDto;
import com.rangein.detective.ai_detective.dtos.services.GameSetupInfoDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequestDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageResponseDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiSetupStoryResponseDto;
import com.rangein.detective.ai_detective.contants.AiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AiService {

    private final GptModel gptModel;

    public GameSetupInfoDto setupStory() {
        AiSetupStoryResponseDto detectiveStory = gptModel.setupCrimeStory();
        int randomIndex = (int) (Math.random() * AiConstants.ASSISTANT_INTROS.size());
        String assistantIntro = AiConstants.ASSISTANT_INTROS.get(randomIndex);
        return new GameSetupInfoDto(
                new DetectiveStoryDto(detectiveStory.title(), detectiveStory.parts()),
                assistantIntro,
                detectiveStory.options());
    }


    public AiMessageResponseDto talkToAssistant(AiMessageRequestDto messageRequestDto) {
        var messageHistory = messageRequestDto.messages();
        messageHistory.add(messageRequestDto.userMessage());
        return gptModel.talkToAssistant(messageHistory);
    }

}
