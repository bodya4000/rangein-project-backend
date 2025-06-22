package com.rangein.detective.ai_detective.services.ai;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClient openAIClient;

    public CompletableFuture<String> requestMessage(String jsonMessage) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(jsonMessage)
                .model(ChatModel.GPT_3_5_TURBO)
                .build();

        return openAIClient.async().chat().completions().create(params)
                .thenApply(chatCompletion ->
                        chatCompletion.choices().getFirst().message().content().orElse("")
                );
    }

}
