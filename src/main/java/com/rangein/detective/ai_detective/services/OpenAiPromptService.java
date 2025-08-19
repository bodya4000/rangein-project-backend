package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.rangein.detective.ai_detective.constants.AiConstants;
import com.rangein.detective.ai_detective.constants.prompt.game.GamePrompt;
import com.rangein.detective.ai_detective.dtos.models.game.GameResult;
import com.rangein.detective.ai_detective.dtos.models.game.GameSetup;
import com.rangein.detective.ai_detective.dtos.models.game.GameState;
import com.rangein.detective.ai_detective.dtos.models.game.GameUpdate;
import com.rangein.detective.ai_detective.dtos.models.game.messages.AiCharacterMessage;
import com.rangein.detective.ai_detective.dtos.models.game.chars.AiCharacter;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequest;
import com.rangein.detective.ai_detective.exceptions.game.OutOfGptTokensExceptions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
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


    public GameUpdate talkToAssistant(AiMessageRequest messageRequestDto) {
        try {
            String finalRequest = GamePrompt.getPrompt(messageRequestDto);
            System.out.println(finalRequest);
            String rawResponse = requestMessage(finalRequest);
            System.out.println(rawResponse);
            String cleaned = cleanGptResponse(rawResponse);
            throw new OutOfGptTokensExceptions();
//            return gson.fromJson(cleaned, GameUpdate.class);
        } catch (Exception e) {
            throw new OutOfGptTokensExceptions();
        }

    }

    public GameUpdate talkToAssistantMock(AiMessageRequest messageRequestDto) {
        List<AiCharacterMessage> messages = List.of(
                new AiCharacterMessage("Inspector Reed", "We should recheck the victim's timeline."),
                new AiCharacterMessage("Officer Vega", "I'll pull the witness statements again."),
                new AiCharacterMessage("Dr. Morgan", "I noticed some irregularities in the autopsy report.")
        );

        List<String> options = List.of("Check timeline", "Re-interview witnesses", "Review autopsy");

        GameState gameState =  new GameState(
                new GameResult(false, false, "Case still open. No consensus.", List.of()),
                List.of("Reed asked Vega about fingerprints", "Morgan hesitated on timeline"),
                5,
                "Victim's Apartment – Rain, broken lamp flickers",
                List.of(
                        new AiCharacter("Inspector Reed", "m", "neutral", "Experienced, cautious, watches everyone.", Map.of("Dr. Morgan", "suspicious")),
                        new AiCharacter("Officer Vega", "f", "trust", "Meticulous, trusts user, but unsure of Morgan.", Map.of("Dr. Morgan", "fear")),
                        new AiCharacter("Dr. Morgan", "f", "distrust", "Cold, technical, avoids questions.", Map.of("User", "distrust"))
                ),
                List.of("lamp has partial fingerprint", "timeline mismatch on Morgan", "blood matches victim"),
                Map.of(
                        "Dr. Morgan", 3,
                        "Inspector Reed", 1,
                        "Officer Vega", 0
                ),
                List.of("fingerprint matches Morgan", "Morgan's timeline contradicts report"),
                List.of("Victim's Apartment", "Forensics Lab"),
                List.of("morgan_alibi_questioned", "fingerprint_checked"),
                "Dr. Morgan"
        );


        return new GameUpdate(messages, options, gameState);
    }


    @SneakyThrows
    public GameSetup setupCrimeStory() {
        try {
            String rawResponse = requestMessage(AiConstants.START_PROMPT);
            System.out.println(rawResponse);
            String cleaned = cleanGptResponse(rawResponse);
            GameSetup aiMessageData = gson.fromJson(cleaned, GameSetup.class);
            System.out.println(aiMessageData);
            throw new OutOfGptTokensExceptions();
//            return aiMessageData;
        } catch (Exception e) {
            throw new OutOfGptTokensExceptions();
        }

    }

    public GameSetup setupCrimeStoryMock() {
        List<AiCharacterMessage> messages = List.of(
                new AiCharacterMessage("Inspector Reed", "Morgan was the last to see the victim alive."),
//                new AiCharacterMessage("Officer Vega", "I found traces of sedative in the tea."),
                new AiCharacterMessage("Dr. Morgan", "You think I’d risk everything for this?")
        );

        List<String> options = List.of("Inspect tea", "Question Morgan", "Check victim's room");

        GameState gameState = new GameState(
                new GameResult(false, false, "Investigation intensifies.", List.of("Officer Vega")),
                List.of(
                        "Vega found sedative traces",
                        "Reed suspects Morgan"
                ),
                4,
                "Victim's Home – Dimly lit, overturned furniture",
                List.of(
                        new AiCharacter("Inspector Reed", "m", "trust", "Veteran, thinks logically, respects User.", Map.of("Dr. Morgan", "suspicious")),
                        new AiCharacter("Officer Vega", "f", "neutral", "Detailed, observant.", Map.of("Dr. Morgan", "fear")),
                        new AiCharacter("Dr. Morgan", "f", "distrust", "Icy, formal, hides anger beneath professionalism.", Map.of("User", "distrust"))
                ),
                List.of(
                        "sedative bottle", "broken teacup", "Reed's notes"
                ),
                Map.of(
                        "Dr. Morgan", 3,
                        "Inspector Reed", 1,
                        "Officer Vega", 0
                ),
                List.of(
                        "tea has sedative", "Morgan served last drink"
                ),
                List.of(
                        "Victim's Home", "Morgue", "Rooftop Access"
                ),
                List.of(
                        "sedative_found", "morgan_under_watch"
                ),
                "Dr. Morgan"
        );

        return new GameSetup(
                "Shadows of Truth",
                List.of(
                        "A second body turns up — another investigator.",
                        "Fear spreads among the team. Who’s next?",
                        "The deeper they dig, the closer they get to the killer’s mask."
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
