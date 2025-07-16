package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;


public class AiConstants {
    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT = """
startgame
makenewdetectiveworld
replyjson
{t:s,p:[s],o:[s],m:[{who:s,m:s}],g:{res:{f:b,ok:b,m:s,dead:[s]},events:[s],turn:n,scene:s,chars:[{name:s,gender:m/f,opinion:n}],inv:[s],obj:[s],loc:[s],flags:[s],culprit:s}}
rules
t3w
p = 3–5 items, each 20–30 words
o3x≤3w quick player option
m fromeachplayer
g.res.f=falsestart
g.res.ok=true
g.res.mempty
g.res.dead[]
tn0
einvobjlocflags[]
chars2-4w/basichp100op0
culprit=oneofchars
""";


    public static final String MESSAGE_PROMPT = """
Continue game
Player %s lead
Request json{playerMessage:s,g:%ss}
Response json{m:[{who:s,m:s}],o:[s],g:{res:{f:b,ok:b,m:s,dead:[s]},e:[s],tn:n,s:s,chars:[{n:s,m:s,hp:n,op:n}],inv:[s],obj:[s],loc:[s],flags:[s],culprit:s}}
Rules
m1-10lasttoplayer
o3x≤3w
g.res.fgameend
g.res.okresult
g.res.mresumeiff
g.res.deadonlyextend
ekeyevents
culpritcankilllie
f1ok0ifabsurd/out/neg/allDead/wrongCulprit
f1ok1ifallagretrueCulprit
            """;

    public static final String START_PROMPT_FOR_HUMAN_MORE_READABLE = """
""";

    public static final String MESSAGE_PROMPT_FOR_HUMAN_MORE_READABLE = """
            Continue game
            Player ${param_player_name} leads open world
            Request json:
            {playerMessage:s,g:${game_state}}
    
            Response json:
            {
               "m":[{"who":"s","m":"s"}],
               "o":["s"],
               "g":{
                  "res":{"f":"b","ok":"b","m":"s","dead":["s"]},
                  "e":["s"],
                  "tn":"n",
                  "s":"s",
                  "chars":[
                     {
                        "n":"s",
                        "m":"s",
                        "hp":"n",
                        "op":"n"
                     }
                  ],
                  "inv":["s"],
                  "obj":["s"],
                  "loc":["s"],
                  "flags":["s"],
                  "culprit":"s"
               }
            }
            Rules:
            m — 1–10 msgs, last to player
            o — 3 options, each ≤3 words
            g.res.f — game finished
            g.res.ok — action valid
            g.res.m — summary if finished
            g.res.dead — only append
            e — key events this turn
            culprit — can kill, can lie
    
            f=true, ok=false if:
            • player does absurd
            • player out of context
            • all chars dead
            • all chars op<0
            • all agree wrong culprit
    
            f=true, ok=true if:
            • all agree real culprit
            """;
}