package com.rangein.detective.ai_detective.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.rangein.detective.ai_detective.dtos.services.GameOverview;
import com.rangein.detective.ai_detective.dtos.services.AiMessageRequest;
import com.rangein.detective.ai_detective.dtos.services.GameSetup;
import com.rangein.detective.ai_detective.dtos.services.GameUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AiService {

    private final GptModel gptModel;

//    public GameSetupInfoDto setupStoryV1() throws JsonProcessingException {
//        GameOverview gameOverview = gptModel.setupCrimeStory();
//        int randomIndex = (int) (Math.random() * AiConstants.ASSISTANT_INTROS.size());
//        String assistantIntro = AiConstants.ASSISTANT_INTROS.get(randomIndex);
//        return new GameSetupInfoDto(
//                new DetectiveStoryDto(gameOverview.t(), gameOverview.p()),
//                assistantIntro,
//                gameOverview.o());
//    }

    public GameSetup setupStory() throws JsonProcessingException {
        return gptModel.setupCrimeStory();
    }


    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        return gptModel.talkToAssistant(messageRequestDto);
    }

}
