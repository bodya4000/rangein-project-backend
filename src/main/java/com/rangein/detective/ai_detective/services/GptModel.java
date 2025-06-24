package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageResponseDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiSetupStoryResponseDto;
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

    public AiMessageResponseDto talkToAssistant(List<MessageDto> wholeMessageHistory) {
        String request = gson.toJson(wholeMessageHistory);
        String rawResponse = requestMessage(request);
        String cleaned = cleanGptResponse(rawResponse);
        AiMessageResponseDto aiMessageData = gson.fromJson(cleaned, AiMessageResponseDto.class);
        return new AiMessageResponseDto(GameRole.DETECTIVE_ASSISTANT_AI, aiMessageData.message(), aiMessageData.options());
    }

    public AiSetupStoryResponseDto setupCrimeStory() {
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
    private AiSetupStoryResponseDto parseJsonStory(String json) {
        try {
            return gson.fromJson(json, AiSetupStoryResponseDto.class);
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
