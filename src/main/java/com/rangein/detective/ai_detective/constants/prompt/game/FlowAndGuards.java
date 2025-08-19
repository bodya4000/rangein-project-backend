package com.rangein.detective.ai_detective.constants.prompt.game;

public class FlowAndGuards {

    public static final String FLOW_RULES =
            """
            FLOW RULES (ORDER OF OPERATIONS)
            0) USER INPUT VALIDATION (LOSS OVERRIDE):
               - First, read d.message and check forbidden cases (profanity, gibberish, meta/god-mode, abusive/hateful, empty/meaningless, out-of-context).
               - If any match → immediate LOSS:
                 - set g.result.finished=true, g.result.ok=false, g.result.m="Invalid or abusive input → game lost"
                 - append g.events "Loss condition triggered: [player message]"
                 - increment g.turn by +1
                 - m[] = exactly ONE line from an alive NPC noting the investigation stops
                 - o = []
                 - RETURN JSON immediately. Do NOT allow mafia to act. Ignore all other rules.

            1) If no loss: apply mafia kill ONLY if mafiaShouldKillInThatTurnNewNpc=true.

            2) Generate NPC messages m[], options o[], and apply state updates.
            """;

    public static final String INPUT_BINDING_RULES =
            """
            INPUT BINDING & DELTA
            - Parse player's message into intent/targets. Append to g.events: "user_intent:<intent>|targets:<names>|raw:<d.message>".
            - At least one NPC line MUST explicitly reference the user's intent/target(s) and report a completed past-tense outcome (success/fail).
            - Each valid user action MUST change at least one of:
              g.events | g.objectiveness | g.flags | g.reasoning | g.locations | g.accusationWeight | NPC opinions/relations.
            - If no such change occurs → simulation failure (regenerate once adhering strictly; on second failure end turn as loss with reason in g.result.m).
            """;

    public static final String OPTIONS_RULES =
            """
            OPTIONS COHERENCE
            - o must contain exactly 3 options, each 1–3 words.
            - All options must derive directly from: user's last intent OR new clues/flags created this turn.
            - Generic options ("Look around", "Be careful") are forbidden.
            """;

    public static final String EMOTION_RULES =
            """
            EMOTION & DEATH REACTIONS
            - Immediate kill turn: all alive NPCs must show emotion (≥1 of shock/fear/sadness/suspicion). No one reveals the killer.
            - Next 1–2 turns: at least one alive NPC references the recent death with evolving emotion (grief → paranoia/stress → suspicion). No repeated lines.
            - React ONLY to the NEW death of this turn. Do NOT react again to older deaths.
            """;

    public static final String CONSISTENCY_RULES =
            """
            CONSISTENCY & AUTO-FIX
            - If a dead NPC appears in m[].who or speaks → remove them and log "autofix:dead_npc_speech:<name>" in g.events.
            - If g.mafia appears in g.result.dead → remove and log "autofix:mafia_immune:<name>" in g.events.
            - Normalize g.npcs[].opinion to {trust|neutral|distrust}. If other values found → normalize and log "autofix:opinion_normalized:<name>:<newOpinion>".
            - Duplicate evidence/flags/reasoning are forbidden; avoid duplicates.
            """;

    public static final String SELF_CHECK_RULES =
            """
            SELF-CHECK BEFORE OUTPUT
            - Ensure:
              - m[] has ≥1 line, only alive NPCs speak, last line addresses the user by name/role.
              - g.events includes "user_intent:" for this turn.
              - DELTA satisfied (at least one state change as defined).
              - o has exactly 3 coherent options (except LOSS where o=[]).
            - If any check fails → regenerate once strictly; if still fails → end as loss with reason "simulation failed to apply player input".
            """;

    public static final String VAGUE_POLICY =
            """
            VAGUE INPUT POLICY
            - If message is vague but not forbidden:
              - One NPC must ask one pointed clarifying question AND propose one concrete next action derived from context.
              - If two vague inputs in a row → raise distrust of at least one NPC toward the user and append g.flags += ["vague_input"].
            """;

    public static final String PACE_RULES =
            """
            PROGRESS PACE
            - Every ≤2 turns produce at least one: new evidence, contradiction, or a noticeable trust/distrust shift toward a likely mafia.
            - If natural progress is absent, derive a clue from environment/relations/inconsistencies and log it.
            """;
}
