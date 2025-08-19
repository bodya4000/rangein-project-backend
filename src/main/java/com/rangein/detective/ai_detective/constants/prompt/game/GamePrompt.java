package com.rangein.detective.ai_detective.constants.prompt.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Locale;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequest;

public class GamePrompt {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private static final String INTRO = """
You are the engine behind an interactive detective mafia game.
You simulate the world and control all NPCs. Respond only in JSON.

user name:%s
Game is mobile-based. Short vivid NPC messages and short options.
User messages are always directed only to NPCs in the game.
Never miss or ignore any instruction in this prompt.
""";

    private static final String USER_INPUT = """
user json input: %s
""";

    public static String getPrompt(AiMessageRequest aiMessageRequest) {
        final String sender = (aiMessageRequest != null && aiMessageRequest.d() != null && aiMessageRequest.d().sender() != null)
                ? aiMessageRequest.d().sender()
                : "player";

        final String requestJson = GSON.toJson(aiMessageRequest);

        return String.format(Locale.ROOT, INTRO, sender) + "\n" +
                GameSkeleton.JSON_STRUCTURE + "\n" +
                Rules.MAFIA_RULES + "\n" +
                Rules.NPC_RULES + "\n" +
                Rules.USER_RULES + "\n" +
                Rules.STATE_UPDATE_RULES + "\n" +
                FlowAndGuards.FLOW_RULES + "\n" +
                FlowAndGuards.INPUT_BINDING_RULES + "\n" +
                FlowAndGuards.OPTIONS_RULES + "\n" +
                FlowAndGuards.EMOTION_RULES + "\n" +
                FlowAndGuards.CONSISTENCY_RULES + "\n" +
                FlowAndGuards.SELF_CHECK_RULES + "\n" +
                FlowAndGuards.VAGUE_POLICY + "\n" +
                FlowAndGuards.PACE_RULES + "\n" +
                GameTon.GAME_TON + "\n\n" +
                String.format(Locale.ROOT, USER_INPUT, requestJson);
    }
}
