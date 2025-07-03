package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.services.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GptModel {

    private final OpenAIClient openAIClient;
    private final Gson gson;

//    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
//        String request = gson.toJson(messageRequestDto);
//        System.out.println(request);
//        String rawResponse = requestMessage(request);
//        System.out.println(rawResponse);
//        String cleaned = cleanGptResponse(rawResponse);
//        GameUpdate aiMessageData = gson.fromJson(cleaned, GameUpdate.class);
//        System.out.println(aiMessageData);
//        return aiMessageData;
//    }

    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        return new GameUpdate(
                "‘You sure about that?’ Rosa narrowed her eyes.",
                List.of(
                        "Rosa slammed the photo on the table.",
                        "'That's your guy,' she said, lighting a cigarette.",
                        "I flipped it over — scrawled date, smeared blood.",
                        "‘Why now?’ I asked. ‘Why here?’",
                        "The ceiling fan hummed like a clock counting down."
                ),
                List.of(
                        "Interrogate suspect",
                        "Examine photo",
                        "Smell cigarette"
                ),
                new GameState(
                        "interrogation_room",
                        List.of(
                                new SuspectJson("Vinny Black", "tense"),
                                new SuspectJson("Rosa Vane", "calm")
                        ),
                        List.of("blood-stained photo", "suspect alibi"),
                        List.of("break Vinny", "verify photo origin"),
                        "Inside job by Vinny",
                        "Vinny Black"
                ),
                false,
                true
        );
    }

//    public GameOverview setupCrimeStory() throws JsonProcessingException {
//        String rawResponse = requestMessage(AiConstants.START_PROMPT);
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
//        String prettyJson = writer.writeValueAsString(mapper.readTree(rawResponse));
//        System.out.println(prettyJson);
//        String cleaned = cleanGptResponse(rawResponse);
//        return parseJsonStory(cleaned);
//    }
public GameSetup setupCrimeStory() {
    List<SuspectJson> suspects = List.of(
            new SuspectJson("Vinny Black", "anxious"),
            new SuspectJson("Lydia Kane", "cold")
    );
    GameState gameState = new GameState(
            "office",
            suspects,
            List.of("silver locket"),
            List.of("find owner", "check prints"),
            "jealousy motive",
            "bob"
    );
    return new GameSetup(
            "The Case of the Silver Locket",
            List.of(
                    "The rain drummed against the window. A silver locket lay on my desk.",
                    "'You’re late,' said Rosa, tossing a folder my way.",
                    "The jazz outside muffled the tension in the room."
            ),
            List.of(
                    "Inspect the locket",
                    "Ask Rosa about the folder",
                    "Stare at the jazz"
            ),
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

    @SneakyThrows
    private GameOverview parseJsonStory(String json) {
        try {
            return gson.fromJson(json, GameOverview.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Failed to parse GPT story JSON: " + json, e);
        }
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
