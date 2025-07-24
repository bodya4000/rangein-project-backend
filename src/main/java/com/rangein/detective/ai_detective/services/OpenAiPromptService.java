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
import java.util.Map;


@Service
@RequiredArgsConstructor
public class OpenAiPromptService {

    private final OpenAIClient openAIClient;
    private final Gson gson;

//    private String minifyPrompt(String input) {
//        return input
//                .replaceAll("[\\s,-]+", "")
//                .trim();
//    }


    @SneakyThrows
    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        String requestJson = gson.toJson(messageRequestDto);
        String name = messageRequestDto.d().sender();
        String finalRequest = (String.format(AiConstants.MESSAGE_PROMPT, name, requestJson));
        System.out.println(finalRequest);
        String rawResponse = requestMessage(finalRequest);
        System.out.println(rawResponse);
        String cleaned = cleanGptResponse(rawResponse);
        GameUpdate aiMessageData = gson.fromJson(cleaned, GameUpdate.class);
//        System.out.println(aiMessageData);
        return aiMessageData;
    }

    public GameUpdate talkToAssistantMock(AiMessageRequest messageRequestDto) {
        List<AiCharacterMessage> messages = List.of(
                new AiCharacterMessage("DetectiveA", "I think we should investigate the library."),
                new AiCharacterMessage("DetectiveB", "Agreed. There’s something odd there.")
        );

        List<String> options = List.of("Go to library", "Interrogate suspect", "Examine body");

        GameState gameState = new GameState(
                new GameResult(false, false, "Turn continued", List.of("DetectiveB")),
                List.of("Interrogation started"),
                1,
                "crime_scene",
                List.of(
                        new AiCharacter("DetectiveA", "f", "neutral", "", Map.of()),
                        new AiCharacter("DetectiveB", "m", "neutral", "", Map.of())
                ),
                List.of("notebook", "flashlight"),
                List.of("Find clue", "Interrogate suspects"),
                List.of("Library", "Kitchen"),
                List.of("interrogation_started"),
                "DetectiveA"
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
                new AiCharacterMessage("DetectiveA", "I think I found a clue."),
                new AiCharacterMessage("DetectiveB", "Let's interrogate the witness.")
        );

        List<String> options = List.of("Look around", "Interview suspect", "Check inventory");

        GameState gameState = new GameState(
                new GameResult(false, true, "", List.of()),
                List.of(), // events порожній
                0, // turn
                "Crime Scene", // scene
                List.of(
                        new AiCharacter("DetectiveA", "f", "neutral", "", Map.of()),
                        new AiCharacter("DetectiveB", "m", "neutral", "", Map.of())
                ),
                List.of(), // inv
                List.of(), // obj
                List.of(), // loc
                List.of(), // flags
                "DetectiveB" // culprit
        );

        return new GameSetup(
                "Detective World", // title
                List.of(
                        "Investigate a mysterious crime scene filled with clues and red herrings.",
                        "Interview suspicious characters while gathering evidence to piece together the truth.",
                        "Unravel a complex web of deceit that leads to unexpected twists."
                ),
                options,
                messages,
                gameState
        );
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
