package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.services.*;
import com.rangein.detective.ai_detective.dtos.services.Character;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GptModel {

    private final OpenAIClient openAIClient;
    private final Gson gson;

    @SneakyThrows
    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        String request = gson.toJson(messageRequestDto);
        String finalRequest = String.format(AiConstants.MESSAGE_PROMPT, request);
        System.out.println(finalRequest);
        String rawResponse = requestMessage(finalRequest);
        System.out.println(rawResponse);
        String cleaned = cleanGptResponse(rawResponse);
        GameUpdate aiMessageData = gson.fromJson(cleaned, GameUpdate.class);
        System.out.println(aiMessageData);
        return aiMessageData;
    }

//    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
//        return new GameUpdate(
//                List.of(
//                        new CharacterMessage("Greta", "Він бреше. Я відчуваю."),
//                        new CharacterMessage("Marco", "Я був там. І що?")
//                ),
//                List.of(
//                        "запитати Марко",
//                        "оглянути ключ",
//                        "вийти надвір"
//                ),
//                new GameState(1,
//                        "crime_scene",
//                        List.of(
//                                new Suspect("Marco", "підозрілий"),
//                                new Suspect("Greta", "мовчазна")
//                        ),
//                        List.of(
//                                "кров на підлозі",
//                                "ключ без відбитків"
//                        ),
//                        List.of(
//                                "встановити власника ключа",
//                                "зламати мовчання Грети"
//                        ),
//                        "Марко підкинув ключ, щоб відвести увагу",
//                        "Marco",
//                        List.of(
//                                new Character("Greta", "f", 36, "бармен", "обережна"),
//                                new Character("Marco", "m", 41, "сусід", "ворожий")
//                        )
//                ),
//                false,
//                true
//        );
//    }


    @SneakyThrows
    public GameSetup setupCrimeStory() {
        String rawResponse = requestMessage(AiConstants.START_PROMPT);
        System.out.println(rawResponse);
        String cleaned = cleanGptResponse(rawResponse);
        GameSetup aiMessageData = gson.fromJson(cleaned, GameSetup.class);
        System.out.println(aiMessageData);
        return aiMessageData;
    }

//    public GameSetup setupCrimeStory() {
//        return new GameSetup(
//                "Mock Case",
//                List.of(
//                        new CharacterMessage("Elsa", "Він щось сховав"),
//                        new CharacterMessage("Bruno", "Це не моя справа"),
//                        new CharacterMessage("Ferre", "Мовчання — золото")
//                ),
//                List.of(
//                        "Я зайшов до майстерні. Троє людей і шестерня на підлозі.",
//                        "Бруно скривився, Ельза мовчала. Ферре пила чай.",
//                        "На шестерні напис: 'F13-B'. Може, це код?",
//                        "У підвалі — ще одна. Ельза тремтить. Тиша згори."
//                ),
//                List.of("перевірити підвал", "спитати Ельзу", "піти"),
//                new GameState(
//                        "",
//                        List.of(),
//                        List.of(),
//                        List.of(),
//                        "",
//                        "",
//                        List.of(
//                                new Character("Elsa", "f", 22, "учениця", "нервова"),
//                                new Character("Bruno", "m", 46, "майстер", "ворожий"),
//                                new Character("Ferre", "f", 58, "вдова", "нейтральна")
//                        )
//                )
//        );
//    }
//


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
