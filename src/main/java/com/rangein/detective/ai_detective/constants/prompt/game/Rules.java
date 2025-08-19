package com.rangein.detective.ai_detective.constants.prompt.game;

public class Rules {

    public static final String MAFIA_RULES =
            """
            MAFIA RULES
            - Exactly one mafia exists in the game. g.mafia remains fixed once set.
            - Mafia can kill NPCs, but never the user.
            - Mafia may lie or accuse, but not in every turn (must vary).
            - Mafia cannot be killed or added to g.result.dead. If g.mafia appears in g.result.dead → game logic failure.
            - Mafia must perform a kill this turn if and only if "mafiaShouldKillInThatTurnNewNpc" = true in user input.
            """;

    public static final String NPC_RULES =
            """
            NPC RULES
            1. Investigating bodies:
            - If user says to investigate the body:
              - If a doctor or officer NPC is alive, one of them MUST investigate and give a past-tense factual response with concrete findings.
              - Save this in g.events as "investigation_result:<finding>".
              - If no doctor or officer is alive, another NPC must say clearly that there is no specialist to investigate the body.
              - Never ignore or defer this action.
            
            2. Moving to locations:
            - If user says "go to <place>":
              - One NPC must immediately perform the action and return with a past-tense factual response.
              - If <place> is new, add it to g.locations.
              - Save this in g.events as "location_checked:<place>".
              - 50% chance: the NPC finds mafia-related evidence (add to g.objectiveness and optionally g.reasoning).
              - 50% chance: the NPC returns with "I went there and saw nothing."
              - Only factual responses allowed. No filler text.
            
            3. Accusations:
            - If user message starts to blame someone:
              - Increase accusationWeight[target] by +1.
              - If accusationWeight[target] > 2, NPCs may begin openly supporting or challenging this blame in dialogue.
              - If ≥50% of alive NPCs (including user) blame the same person:
                - If that person = g.mafia → game won: set finished=true, ok=true, update g.result.m with win message.
                - If that person ≠ g.mafia → game lost: set finished=true, ok=false, update g.result.m with loss message.
            
            4. Dead NPCs:
            - If an NPC is listed in g.result.dead:
              - They CANNOT speak or appear in m[].who.
              - Their accusationWeight no longer matters. Exclude them from all suspicion or voting logic.
            
            5. Mafia kill reactions:
            - If "mafiaShouldKillInThatTurnNewNpc" = true in user input:
              - Mafia must kill exactly one eligible NPC (non-user, non-mafia, not already dead).
              - Add killed NPC to g.result.dead.
              - Remove them from all future m[].who.
              - All remaining alive NPCs MUST immediately react emotionally this turn (shock, fear, sadness, suspicion).
              - Update NPC opinions/opinionLong to reflect the death.
              - Append an event to g.events describing the kill.
              - Update g.result.m with an info message for the user about this death.
            """;

    public static final String USER_RULES =
            """
            USER RULES
            - The user is NOT an NPC. Never add the user to g.npcs.
            - The user must NEVER appear in g.result.dead. The user cannot be killed or targeted by mafia.
            - The user must NEVER be g.mafia. If input state violates this, auto-correct and append a fix note to g.events.
            - Only NPCs speak in m[]. The user never speaks in m[]. The last NPC line must address the user by name/role.
            - The user may accuse, question, or command NPCs. The user’s accusation counts toward the ≥50% blame threshold.
            - The user does NOT have accusationWeight; do not add user to g.accusationWeight.
            - The user cannot unilaterally end or rewrite the story. Win/loss happens only by the defined conditions.
            - Each valid user message MUST cause progress in at least one: g.events | g.objectiveness | g.flags | g.reasoning | locations | opinions/relations | accusationWeight.
            - If the user requests an action requiring a specific role (e.g., doctor/officer/forensics) and such NPC is dead/absent, an alive NPC must explicitly state the limitation and propose the closest feasible alternative. Log this in g.events.
            - If the user targets a dead/absent NPC or asks for an impossible action, an alive NPC must correct and redirect, and the correction must be logged in g.events.
            - User messages are always directed ONLY to NPCs in the game.
            """;

    public static final String STATE_UPDATE_RULES =
            """
            STATE UPDATE RULES
            - accusationWeight:
              - Always update when blame occurs.
              - Dead NPCs are excluded from accusationWeight counts.
              - Only alive NPCs and user accusations count toward ≥50% threshold.
            - locations:
              - Add to g.locations only if new and explicitly mentioned (e.g., user "go to <place>").
              - Never remove old locations. Keep cumulative history.
            - objectiveness:
              - Add new evidence/facts only if concrete, mafia-related, or confirmed by investigation.
              - Never duplicate identical entries.
            - reasoning:
              - Append only short factual logic lines directly derived from objectiveness or accusations.
            - flags:
              - Use for tracking milestones, discoveries, or one-time events (e.g., "first_kill", "evidence_found:kitchen").
            - events:
              - Every investigation, location check, kill, accusation, or rule-triggered correction MUST append a descriptive event line.
            - turn:
              - Always increment g.turn by +1 after processing each valid user message, regardless of outcome.
            """;

}
