package com.rangein.detective.ai_detective.services.ai;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAiService openAIClient;
    private final GeminiAiService geminiAiService;

    public CompletableFuture<String> requestOpenAiMessage(String jsonMessage) {
        return openAIClient.requestMessage(jsonMessage);
    }

    public CompletableFuture<String> requestGeminiMessage(String jsonMessage) {
        return geminiAiService.requestMessage(jsonMessage);
    }


    public CompletableFuture<List<String>> requestGeminiSetup() {
        return geminiAiService.makeUpHistory();
    }



}
