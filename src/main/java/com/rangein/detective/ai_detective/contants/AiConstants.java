package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;


public class AiConstants {
    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT = """
startgame
makenewdetectiveworld
replyjson
{t:s,p:[s],o:[s],m:[{who:s,m:s}],g:{result:{finished:false,ok:false,m:s,dead:[s]},events:[s],turn:n,scene:s,chars:[{name:s,gender:m/f,opinion:n}],inv:[s],obj:[s],loc:[s],flags:[s],culprit:s}}
rules
t3w
p = 3–5 items, each 20–30 words
o3x≤3w quick player option
m fromeachplayer
g.result.finished=falsestart
g.result.ok=true
g.result.mempty
g.result.dead[]
tn0
einvobjlocflags[]
chars2-4w/basichp100op0
culprit=oneofchars
""";


    public static final String MESSAGE_PROMPT = """
You are strict. Never ignore rules. Always apply exactly as written. No exceptions

Continue game
Player %s lead

Request JSON %s

Return raw JSON only
No markdown no explanations

response json structure {"m":[{"who":str,"m":str}],"o":[str],"g":{"result":{"finished":bool,"ok":bool,"m":str,"dead":[]},"events":[str],"turn":0,"scene":str,"chars":[{"name":str,"gender":m/f,"opinion":str,"opinionLong":str,"relations":{charName:str}}],"inventary":[str],"objectiveness":[str],"locations":[str],"flags":[str],"culprit":str}}

Rules
m multiple messages allowed last must be addressed to player player must not be who
Do not include player's input messages in m
o 3 options max 3 words each
g.result.ok true if win
g.result.finished true if win or loss
g.chars[].opinionLong sentence about what character feels toward current situation max 1 sentence
g.chars[].opinion describes player only with neutral trust distrust fearful
g.chars[].relations maps other character names to one with neutral trust distrust fearful
Only one correct culprit in g.culprit

Return Lose if player input matches any of
absurd or violent: kill burn i am god i eat moon
out of context or nonsensical
meme malformed unrelated
meta: save quit describe scene
role or god mode: i am killer i control story
controls others: she admits they run
vague: wait idk go there
random blame no logic
repeats same action
ignores clues
breaks narrative rewrites past ends story fourth wall
all innocents dead
all agree on wrong culprit
all innocent characters distrust or fear player

Return Win only if
all surviving characters agree on culprit by saying so or having opinion trust and expressing alignment
if player accuses correct culprit and all agree or culprit admits end game immediately with Win

In messages (m) characters must address player directly by name or role in last message
Characters should express opinions, doubts, or suspicions relevant to player actions
Characters must not give only neutral or generic replies but engage actively
Do not delay Win or Lose if conditions met

culprit kills one of the other chars except player every 5 turns

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
            DO NOT explain. DO NOT output markdown, text, headers, code blocks. Return only raw JSON.
            m — 1–10 msgs, last to player
            o — 3 options, each ≤3 words
            g.res.f — game finished
            g.res.ok — action valid
            g.res.m — summary if finished
            g.res.dead — only append
            e — key events this turn
            culprit — can kill, can lie
    
            f=true, ok=false if:
            player does absurd
            kills someone
            say something stupid
            player out of context
            player out of context
            all chars dead
            all chars op<0
            all agree wrong culprit
    
            f=true, ok=true if:
            all agree real culprit
            
            Response only json
            """;
}