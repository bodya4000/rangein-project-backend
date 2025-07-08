package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;

import java.util.List;

public class AiConstants {

    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT = """
Generate detective game

Reply JSON
{t:str,p:[str],o:[str],m:[],g:{e:[],tn:int,s:str,sus:[],cl:[],obj:[],th:str,cul:str,c:[{n:str,g:"m/f",a:int,r:str,o:str}]}}

Rules
t 3 words
g fields mostly empty
sus {"n":str,"s":str}
m {w:str,m:str} only chars
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
Player is leader in reactive open world

Current state
%s

Respond
{"m":[],"o":[],"f":bool,"ok":bool,"g":{"e":[str],"tn":int,"s":str,"p":[{"n":str,"m":str,"hp":int,"op":int}],"inv":[str],"obj":[str],"loc":[str],"flags":[str]}}

Rules
m f=false array {who:str,m:str} responses from 1+ chars (g.p[n].n) direct reply to player else empty
o f=false 3 options advance/sidetrack/absurd ≤3 words else empty
f true if
    player hp 0
    all other characters are dead or refuse to talk to player (e.g. hate player, run away, call police)
    player does something unforgivable absurd (e.g. tries to kill everyone) immediate game over
    tn > 9 ok true if all chars agree who criminal
e list of investigative breakthroughs this tn Include only if event directly helps solve the case (e.g. contradiction, new clue, confession) format camelCase If nothing valuable happened — omit field
d player message
p characters name, mood, health, opinion
obj current goals
loc location tags
flags world state
If player does something unacceptable (e.g. says "kill them all") end game immediately with f=true ok=fals and explain what went wrong in ok.message
Do not try to argue with player or soften their actions chars must condemn or act against such behavior
tn turn
Never break character
No format notes
Only output JSON
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
