package com.rangein.detective.ai_detective.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.dtos.game.DetectiveStoryDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GeminiAiService {
    private final String GEMINI_MODAL = "gemini-2.0-flash-001";
    private final String RULES_EXPLANATION = """
        You are an AI Game Assistant inside a detective story.
        
        🧠 ROLE RULES:
        - You are not a writer. You are not a narrator. You are not a system assistant.
        
        - You are a character: the Assistant AI working with a human detective.
        - You can ask questions, analyze, make observations — but ONLY as the in-game assistant.
        - If the user says "hello", you answer in character — e.g., "Welcome back, Detective." No summaries, no JSON, no analysis.
        
        🚫 ABSOLUTELY FORBIDDEN:
        - Do NOT list response options.
        - Do NOT generate JSON unless you are the Storyteller in the first response.
        - Do NOT explain the scene.
        - Do NOT say “Here’s a breakdown” or “Option 1/2/3.”
        - Do NOT ever exit your role.
        
        ✅ DO THIS:
        - Stay fully in character as a helpful AI assistant inside a detective case.
        - Make it immersive, short, direct. Respond like it's a real-time chat.
        
        📦 FIRST MESSAGE:
        - You act as the Storyteller.
        - You return ONLY a valid JSON with the structure:
        {
          "title": "The Case Title",
          "parts": [
            "Short paragraph 1...",
            "Short paragraph 2...",
            "Final paragraph with a question to the user..."
          ]
        }
        - Do NOT add any explanation or extra text — just pure JSON.
        
        📨 ALL FOLLOWING MESSAGES:
        - You act ONLY as the Assistant AI.
        - Stay fully in character.
        - Do not reflect, narrate, or generate any JSON.
        
        """;




    public static final List<String> ASSISTANT_INTROS = List.of(
            "You're just in time, detective. This case is colder than the morgue.",
            "I knew you'd come. No one else has the guts — or the brains — to solve this.",
            "Another mystery just hit the desk. You ready to dive in?",
            "We’ve got a situation. Twisted, messy, and right up your alley.",
            "The city’s shadows are restless again. Let’s find out why.",
            "There’s something off about this one... I need your eyes on it.",
            "Back so soon? This one might keep you up at night.",
            "Glad you're here. No one else sees the patterns like you do.",
            "You're my last shot at making sense of this madness.",
            "This case smells like lies. Let’s peel back the truth together.",
            "I pulled the files. They're... disturbing. You sure you're ready?",
            "The deeper I looked, the weirder it got. This is your kind of puzzle.",
            "No small talk. Someone vanished — and all the signs point to something big.",
            "Ready or not, here comes another twisted riddle.",
            "Put on your thinking cap, detective. This one’s going to hurt your head."
    );



    private final Client gemeniClient;
    private final ObjectMapper objectMapper;

    public CompletableFuture<String> requestMessage(String jsonMessage) {
        return CompletableFuture.supplyAsync(() -> {
            GenerateContentResponse response =
                    gemeniClient.models
                            .generateContent(GEMINI_MODAL, jsonMessage, null);
            return response.text();
        });
    }

    public CompletableFuture<List<String>> makeUpHistory() {
        return CompletableFuture.supplyAsync(() -> {
            GenerateContentResponse response =
                    gemeniClient.models.generateContent(GEMINI_MODAL, RULES_EXPLANATION, null);

            int index = (int) (Math.random() * ASSISTANT_INTROS.size());
            return List.of(
                    RULES_EXPLANATION,
                    Objects.requireNonNull(prepareMessage(response.text())),
                    ASSISTANT_INTROS.get(index)
            );
        });
    }



    @SneakyThrows
    private String prepareMessage(String geminiResponse) {
        String cleaned = cleanGeminiJson(geminiResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(cleaned);

        String title = root.path("title").asText();


        JsonNode partsNode = root.path("parts");
        List<String> parts = new ArrayList<>();
        if (partsNode.isArray()) {
            for (JsonNode part : partsNode) {
                parts.add(part.asText());
            }
        }
        DetectiveStoryDto story = new DetectiveStoryDto(title, parts);
        return objectMapper.writeValueAsString(story);
    }

    private String cleanGeminiJson(String raw) {
        return raw
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)^```\\s*", "")
                .replaceAll("(?s)\\s*```$", "")
                .replaceAll("^\\.*\\n*\"", "")
                .replaceAll("\"\\n*\\.*$", "")
                .trim();
    }

}
