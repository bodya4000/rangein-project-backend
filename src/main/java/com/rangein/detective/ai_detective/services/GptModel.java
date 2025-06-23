package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.game.AiMessageDto;
import com.rangein.detective.ai_detective.dtos.game.AiSetupResponse;
import com.rangein.detective.ai_detective.dtos.game.DetectiveStoryDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GptModel {

    private final OpenAIClient openAIClient;
    private final Gson gson;

    public AiMessageDto talkToAssistant(List<AiMessageDto> wholeMessageHistory) {
        String request = gson.toJson(wholeMessageHistory);
        String rawResponse = requestMessage(request);
        String cleaned = cleanGptResponse(rawResponse);
        AiMessageDto aiMessageData = gson.fromJson(cleaned, AiMessageDto.class);
        return new AiMessageDto(GameRole.DETECTIVE_ASSISTANT_AI, aiMessageData.message(), aiMessageData.options());
    }

    public AiSetupResponse setupCrimeStory() {
        String rawResponse = requestMessage(AiConstants.START_PROMPT);
        String cleaned = cleanGptResponse(rawResponse);
        return parseJsonStory(cleaned);
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
    private AiSetupResponse parseJsonStory(String json) {
        try {
            return gson.fromJson(json, AiSetupResponse.class);
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
