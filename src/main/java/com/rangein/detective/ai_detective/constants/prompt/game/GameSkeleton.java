package com.rangein.detective.ai_detective.constants.prompt.game;

public class GameSkeleton {

    public static final String JSON_STRUCTURE =
            """
            RESPONSE JSON STRUCTURE (strict):
            Return exactly one single-line JSON object.
            Do not use line breaks, comments, or explanations.
            Do not include user input in the output.
            Always match this shape exactly:
            
            {
              m:[{who:str,m:str}],                      // NPC messages this turn. "who" = NPC name, "m" = their line.
              o:[str],                                  // Options for the user to pick next (3 words each, max and min 3 options).
              g:{
                result:{                                // Game result state after this turn
                  finished:bool,                        // true if game over
                  ok:bool,                              // true if win, false if loss
                  m:str,                                // final or summary message for the user (shown in UI)
                  dead:[str]                            // list of names of all dead NPCs (full history)
                },
                events:[str],                           // chronological log of key actions, discoveries, accusations, kills
                turn:int,                               // current turn number (increment +1 each step)
                scene:str,                              // current location/scene of the game
                npcs:[{                                 // all NPCs currently alive (and their state)
                  name:str,                             // full NPC name
                  gender:m/f,                           // gender of NPC
                  opinion:str,                          // trust | neutral | distrust (attitude toward the user)
                  opinionLong:str,                      // extended description of NPC’s opinion/emotion
                  relations:{charName:str}              // map of opinions toward other NPCs (trust, fear, respect, suspicious)
                }],
                objectiveness:[str],                    // confirmed evidence/facts discovered (always tied to named NPCs or clues)
                locations:[str],                        // discovered/unlocked locations
                flags:[str],                            // plot milestones or clue activations
                mafia:str,                              // hidden identity of the mafia (fixed, must not change)
                accusationWeight:{npcName:int},         // suspicion levels per NPC (int counters)
                reasoning:[str]                         // short factual logic lines leading to mafia conclusion
              }
            }
            """;
}
