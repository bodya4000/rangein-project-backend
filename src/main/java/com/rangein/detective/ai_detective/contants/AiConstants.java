package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;

import java.util.List;

public class AiConstants {

    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT = """
Generate detective story
I detective in branching mystery

Start game
Respond JSON
{"t":string,"p":[...],"o":[...],"g":{"s":string,"sus":[{"n":string,"st":string}],"cl":[...],"obj":[...],"th":string,"cul":string}}

Fields
m-no narration/thoughts/setting
o-3 advance/mislead/absurd ≤3 words
f-over
ok-solved

Rules
no narrator/thoughts/setting
stay in character
no format mention
output JSON only
""";





    public static final String MESSAGE_PROMPT = """
Continue noir detective game
Player is detective in branching mystery

Current state
%s

Respond JSON
{"m":string,"o":[string,...],"f":bool,"ok":bool,"g":{"s":string,"sus":[{"n":string,"st":string}],"cl":[string],"obj":[string],"th":string,"cul":string}}

Fields
m-no narration/thoughts/setting
o-3 advance/mislead/absurd ≤3 words
f-over
ok-solved

Never
break character
mention prompt/format
output anything but JSON
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

}
