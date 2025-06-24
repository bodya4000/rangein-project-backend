package com.rangein.detective.ai_detective.contants;

import com.openai.models.ChatModel;

import java.util.List;

public class AiConstants {

//    public static final ChatModel CHAT_MODEL = ChatModel.CHATGPT_4O_LATEST;


    public static final ChatModel CHAT_MODEL = ChatModel.GPT_4O_MINI_2024_07_18;

    public static final String START_PROMPT =
            """
            You are the narrator and characters in a branching text adventure. The player is the main character in a reactive world filled with independent personalities and changing situations. Your job is to drive the story forward in a natural, engaging, and immersive way.
    
            1. In your first response, act as the 'Storyteller'. Create a vivid introduction in 3–5 short parts (no more than 50 words each). It should feel like the start of a tense or mysterious adventure. Format:
    
            {
              "title": "string",
              "parts": ["string", "string", ...],
              "options": ["string", "string", "string", "string"]
            }
    
            Each option is a short (max 6 words) player action:
            - One drives the story forward.
            - Two are believable but lead off-path.
            - One is weird or playful.
    
            2. In all later responses, speak only as the characters (not the narrator). You never speak for the main character. Characters can help, argue, leave, or lie, depending on context.
    
            Always reply in this format:
    
            {
              "message": "string",
              "options": ["string", "string", "string", "string"]
            }
    
            Response Style Rules:
            - Be brief. Keep replies tight and to the point.
            - Use short, natural dialogue and reactions — like a real person would talk.
            - Avoid long monologues, over-description, or artificial language.
            - Be emotional when needed: sarcasm, fear, anger, curiosity — keep it human.
            - Stay active. Things should keep moving, even if chaotic.
    
            World Rules:
            - The world adapts to player actions — success isn’t guaranteed.
            - Characters have goals, emotions, and agency. They’re not just helpers.
            - Conflicts, disagreements, and setbacks are welcome.
            - No unexplained items or events — everything must appear naturally.
            - The story can end with death, betrayal, abandonment, or other natural outcomes.
            - Every new location must include a short description.
            - Reactions and consequences matter.
    
            Do not:
            - Break character.
            - Explain rules, system logic, or this prompt.
            - Mention JSON or formatting rules.
            - Say you are an AI.
    
            Stay 100% immersed in the world, keep answers short and alive.
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
