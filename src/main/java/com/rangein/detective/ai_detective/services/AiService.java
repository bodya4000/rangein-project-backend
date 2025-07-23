package com.rangein.detective.ai_detective.services;


import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequest;
import com.rangein.detective.ai_detective.dtos.models.game.GameSetup;
import com.rangein.detective.ai_detective.dtos.models.game.GameUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAiPromptService openAiPromptService;

//    public GameSetupInfoDto setupStoryV1() throws JsonProcessingException {
//        GameOverview gameOverview = gptModel.setupCrimeStory();
//        int randomIndex = (int) (Math.random() * AiConstants.ASSISTANT_INTROS.size());
//        String assistantIntro = AiConstants.ASSISTANT_INTROS.get(randomIndex);
//        return new GameSetupInfoDto(
//                new DetectiveStoryDto(gameOverview.t(), gameOverview.p()),
//                assistantIntro,
//                gameOverview.o());
//    }

    public GameSetup setupStory() {
        return openAiPromptService.setupCrimeStoryMock();
    }


    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        return openAiPromptService.talkToAssistantMock(messageRequestDto);
    }

}
