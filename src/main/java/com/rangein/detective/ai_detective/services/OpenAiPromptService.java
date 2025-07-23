package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.models.game.GameResult;
import com.rangein.detective.ai_detective.dtos.models.game.GameSetup;
import com.rangein.detective.ai_detective.dtos.models.game.GameState;
import com.rangein.detective.ai_detective.dtos.models.game.GameUpdate;
import com.rangein.detective.ai_detective.dtos.models.game.messages.AiCharacterMessage;
import com.rangein.detective.ai_detective.dtos.models.game.chars.AiCharacter;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OpenAiPromptService {

    private final OpenAIClient openAIClient;
    private final Gson gson;

    @SneakyThrows
    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        String requestJson = gson.toJson(messageRequestDto);
        String name = messageRequestDto.d().sender();
        String finalRequest = String.format(AiConstants.MESSAGE_PROMPT, name, requestJson);
        System.out.println(finalRequest);
        String rawResponse = requestMessage(finalRequest);
        System.out.println(rawResponse);
        String cleaned = cleanGptResponse(rawResponse);
        GameUpdate aiMessageData = gson.fromJson(cleaned, GameUpdate.class);
        System.out.println(aiMessageData);
        return aiMessageData;
    }

    public GameUpdate talkToAssistantMock(AiMessageRequest messageRequestDto) {
        List<AiCharacterMessage> messages = List.of(
                new AiCharacterMessage("Detective Jane", "I think we should investigate the library."),
                new AiCharacterMessage("Detective Mike", "Agreed. There’s something odd there.")
        );

        List<String> options = List.of("Go to library", "Interrogate suspect", "Examine body");

        GameState gameState = new GameState(
                new GameResult(false, false, "Turn continued", List.of("Detective Jane")),
                List.of("Interrogation started"),
                1,
                "crime_scene",
                List.of(
                        new AiCharacter("Detective Jane", "f", "neutral"),
                        new AiCharacter("Detective Mike", "m", "neutral")
                ),
                List.of("notebook", "flashlight"),
                List.of("Find clue", "Interrogate suspects"),
                List.of("Library", "Kitchen"),
                List.of("interrogation_started"),
                "Mr. Smith"
        );

        return new GameUpdate(messages, options, gameState);
    }



    @SneakyThrows
    public GameSetup setupCrimeStory() {
        String rawResponse = requestMessage(AiConstants.START_PROMPT);
        System.out.println(rawResponse);
        String cleaned = cleanGptResponse(rawResponse);
        GameSetup aiMessageData = gson.fromJson(cleaned, GameSetup.class);
        System.out.println(aiMessageData);
        return aiMessageData;
    }

    public GameSetup setupCrimeStoryMock() {
        List<AiCharacterMessage> messages = List.of(
                new AiCharacterMessage("Detective Jane", "The case begins. Stay sharp."),
                new AiCharacterMessage("Detective Mike", "Let’s find the truth.")
        );

        List<String> options = List.of("Look around", "Talk to witnesses", "Check victim");

        GameState gameState = new GameState(
                new GameResult(false, true, "", List.of()),
                List.of(),
                0,
                "intro_scene",
                List.of(
                        new AiCharacter("Detective Jane", "f", "neutral"),
                        new AiCharacter("Detective Mike", "m", "neutral")
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null
        );

        return new GameSetup("Murder at the Manor", List.of("intro", "investigation", "reveal"), options, messages, gameState);
    }


    // *****************     PRIVATE METHODS     *********************

    private String requestMessage(String message) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(message)
                .model(AiConstants.CHAT_MODEL)
                .build();

        return openAIClient.chat()
                .completions()
                .create(params)
                .choices()
                .getFirst()
                .message()
                .content().orElse("");
    }

    private String cleanGptResponse(String raw) {
        return raw
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)^```\\s*", "")
                .replaceAll("(?s)\\s*```$", "")
                .replaceAll("^\\.*\\n*\"", "")
                .replaceAll("\"\\n*\\.*$", "")
                .trim();
    }
}
