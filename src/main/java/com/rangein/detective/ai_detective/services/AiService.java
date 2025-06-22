package com.rangein.detective.ai_detective.services;


import com.rangein.detective.ai_detective.dtos.game.AiMessageDto;
import com.rangein.detective.ai_detective.dtos.game.DetectiveStoryDto;
import com.rangein.detective.ai_detective.dtos.game.GameSetupInfoDto;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.enums.GameRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AiService {

    private final GptModel gptModel;


    public GameSetupInfoDto setupStory() {
        DetectiveStoryDto detectiveStory = gptModel.setupCrimeStory();
        int randomIndex = (int) (Math.random() * AiConstants.ASSISTANT_INTROS.size());
        String assistantIntro = detectiveStory.parts().get(randomIndex);
        return new GameSetupInfoDto(
                detectiveStory,
                new AiMessageDto(GameRole.DETECTIVE_ASSISTANT_AI,assistantIntro));
    }


    public AiMessageDto talkToAssistant(List<AiMessageDto> messageHistory) {
       return gptModel.talkToAssistant(messageHistory);
    }

}
