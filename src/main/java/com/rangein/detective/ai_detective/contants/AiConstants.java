package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;

import java.util.List;

public class AiConstants {
// {t:str,p:[str],o:[str],m:[],g:{e:[],tn:int,s:str,sus:[],cl:[],obj:[],th:str,cul:str,c:[{n:str,g:"m/f",a:int,r:str,o:str}]}}
    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT = """
Generate detective game

Reply JSON
{t:str,p:[str],o:[str],m:[],g:{e:[],tn:int,s:str,sus:[],cl:[],obj:[],th:str,cul:str,c:[{n:str,g:"m/f",a:int,r:str,o:str}]}}

Rules
t 3 words
g fields mostly empty
sus {"n":str,"s":str}
m {who:str,m:str} atleast 1 message
o 3 options ≤3 words mixed style
op trust/neutral/hostile
r job/absurd
p 3–5 parts 20–40 words all chars
no thoughts/narrator/setting
JSON only
≤400 tokens
""";


    public static final String MESSAGE_PROMPT = """
Continue game
Player leads open world

State %s

Respond
{"m":[],"o":[],"f":bool,"ok":bool,"g":{"e":[str],"tn":int,"s":str,"p":[{"n":str,"m":str,"hp":int,"op":int}],"inv":[str],"obj":[str],"loc":[str],"flags":[str]}}

Rules
m f false array {who:str,m:str} else empty
  can be multiple 1–10
  chars may talk to each other accuse argue react
  last message must address player by name
  player replies after

o f false 3 options advance/sidetrack/absurd ≤3 words
f true if
  player hp=0
  all chars refuse/talk dead/hate
  player acts unforgivable (e.g. "kill all")
  tn>9 and all agree who guilty

e breakthroughs (e.g. clue/confession) this tn only camelCase omit if nothing
d player input
g.p name mood hp opinion
g.obj goals
g.loc tags
g.flags world state

If player action violates logic/morality end game
f=true ok=false ok.message what went wrong Don’t argue Stay IC

No narrator No format notes JSON only
""";


    public static final List<String> ASSISTANT_INTROS = List.of(
        "You're just in time, detective. This case is colder than the morgue.",
        "I knew you'd come. No one else has the guts — or the brains — to solve this.",
        "Another mystery just hit the desk. You ready to dive in?",
        "We’ve got a situation. Twisted, messy, and right up your alley.",
        "The city’s shadows are restless again. Let’s find out why.",
        "There’s something off about this onesting I need your eyes on it.",
        "Back so soon? This one might keep you up at night.",
        "Glad you're here. No one else sees the patterns like you do.",
        "You're my last shot at making sense of this madness.",
        "This case smells like lies. Let’s peel back the truth together.",
        "I pulled the files. They'resting disturbing. You sure you're ready?",
        "The deeper I looked, the weirder it got. This is your kind of puzzle.",
        "No small talk. Someone vanished — and all the signs point to something big.",
        "Ready or not, here comes another twisted riddle.",
        "Put on your thinking cap, detective. This one’s going to hurt your head."
        );

}
