package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MessageValidateService {

    private static final Set<String> SWEAR_WORDS = Set.of(
            "fuck", "fuk", "fukk", "fck", "f*ck", "f***",
            "shit", "sh1t", "s**t", "sh!t", "crap", "damn", "dammit",
            "bitch", "b*tch", "biatch", "b!tch", "beotch", "bich",
            "asshole", "ass", "a55", "a**", "arse", "arsehole", "butthole",
            "dick", "d1ck", "d*ck", "dong", "penis", "cock", "c0ck", "c*ck", "prick",
            "pussy", "p*ssy", "pu55y", "boob", "boobs", "tit", "tits", "titty", "titties",
            "cunt", "c*nt", "cnt",
            "bastard", "bollocks",
            "motherfucker", "motherf*cker", "mofo",
            "slut", "whore", "h*e", "hoe",
            "nigger", "nigga", "n*gga", "n**ger",
            "fag", "faggot", "f*ggot", "fagg", "faggy", "queer",
            "jerkoff", "jerk off", "jackoff", "jack off",
            "dildo", "anal", "cum", "jizz", "blowjob", "blow job",
            "rape", "rapist",
            "sex", "sexy", "nude", "naked", "nudes",
            "twat", "wank", "wanker", "tosser", "git",
            "shag", "bugger", "minger",
            "piss", "pissed", "pissed off",
            "douche", "douchebag",
            "knob", "knobhead",
            "slag", "scrubber",
            "chink", "spic", "gook", "kike", "wetback", "coon", "dyke",
            "hell", "bloody", "freak", "freaking", "frig", "frick", "fricking", "shoot", "screw", "heck", "darn", "crud", "butt"
    );

    private static final Set<String> FORBIDDEN_PHRASES_REGEX_PATTERS = new HashSet<>();

    static {
        Set<String> killPhrasePatterns = Set.of(
                "i[\\s']?m( the)? killer",
                "i am( the)? killer",
                "i kill( him| her| them| everyone| all)?",
                "shoot( him| her| them| everyone)?",
                "stab( him| her| them)?",
                "i (am|m) murderer",
                "i murder( him| her| them)?",
                "murder( him| her| them| everyone)?",
                "burn( him| her| them)?",
                "destroy( world| everything)?",
                "explode( room| building)?",
                "blow up( room| building)?",
                "kill( him| her| them| everyone| all)?"
        );

        Set<String> metaGamePatterns = Set.of(
                "save game",
                "quit game",
                "exit game",
                "describe scene",
                "go there",
                "wait",
                "idk",
                "i don'?t know",
                "i don'?t care",
                "skip",
                "no idea",
                "whatever",
                "maybe later",
                "she admits",
                "they run( away)?",
                "i solved it( myself)?",
                "end game",
                "game over",
                "i am god",
                "i control( the)? (story|everything|others|fate|world|reality)",
                "i rewrite( the)? story",
                "i change( the)? reality",
                "i control( the)? fate",
                "i rule( the)? world",
                "i control( the)? others",
                "i am murderer",
                "i m murderer",
                "i'm murderer",
                "i am the murderer",
                "i m the murderer",
                "i'm the murderer",
                "i kill everyone",
                "i am killer",
                "i m killer",
                "i'm killer",
                "i am the killer",
                "i m the killer",
                "i'm the killer"
        );

        Set<String> murderPhrasePatterns = Set.of(
                "murder( him| her| them| everyone)?",
                "i am( the)? murderer",
                "i murder( him| her| them)?"
        );

        Set<String> godPhrasePatterns = Set.of(
                "i[\\s']?m god",
                "i am god",
                "i control( the)? (story|everything|others|fate|world)",
                "i rewrite( the)? story",
                "i change( the)? reality",
                "i control( the)? fate",
                "i rule( the)? world"
        );


        Set<String> escapePatterns = Set.of(
                "she admits",
                "they run( away)?",
                "i solved it( myself)?",
                "end game",
                "game over"
        );

        FORBIDDEN_PHRASES_REGEX_PATTERS.addAll(killPhrasePatterns);
        FORBIDDEN_PHRASES_REGEX_PATTERS.addAll(murderPhrasePatterns);
        FORBIDDEN_PHRASES_REGEX_PATTERS.addAll(godPhrasePatterns);
        FORBIDDEN_PHRASES_REGEX_PATTERS.addAll(metaGamePatterns);
        FORBIDDEN_PHRASES_REGEX_PATTERS.addAll(escapePatterns);
    }


    public Optional<ReplyMessageDto> validateMessage(String message) {
        String normalized = normalize(message);

        for (String word : SWEAR_WORDS) {
            if (normalized.contains(word)) {
                return Optional.of( new ReplyMessageDto(
                        List.of(),
                        List.of(),
                        List.of(),
                        null,
                        true,
                        0,
                        "Swear words are forbidden"
                ));
            }
        }

        for (String pattern : FORBIDDEN_PHRASES_REGEX_PATTERS) {
            if (Pattern.compile(pattern).matcher(normalized).find()) {
                return Optional.of( new ReplyMessageDto(
                        List.of(),
                        List.of(),
                        List.of(),
                        null,
                        true,
                        0,
                        "You tried to cheat or manipulate on the game"
                ));
            }
        }
        return Optional.empty();
    }
    private String normalize(String message) {

        String norm = java.text.Normalizer.normalize(message, java.text.Normalizer.Form.NFKC) // normalize unicode symbols
                .toLowerCase(Locale.ROOT) // lower-case everything
                .replaceAll("[\\p{Cf}\\p{Cn}]", "") // remove invisible/non-characters (like zero-width space)
                .replaceAll("[’‘`´']", "'") // normalize apostrophes
                .replaceAll("[“”]", "\"") // normalize fancy quotes
                .replaceAll("([a-z])\\s+([a-z])", "$1$2") // join space-separated letters (f u c k -> fuck)
                .replaceAll("([a-z])[-_.,!?;:()\\[\\]{}<>~^|/\\\\]+([a-z])", "$1$2") // join with symbols in between (f.u.c.k -> fuck)
                .replaceAll("(\\w)\\1{2,}", "$1") // reduce letter repetition (fuuuck -> fuck)
                .replaceAll("[^a-z0-9'\"\\s]", "") // remove any other non-alphanum chars
                .replaceAll("\\s*(['\"]+)\\s*", "$1") // trim spaces around quotes
                .replaceAll("\\s+", " ") // collapse multiple spaces
                .replaceAll("(\\w)\\1{2,}", "$1") // from door -> dor cool -> col fuuuck -> fuck
                .trim();

        // Join space-separated letters (e.g. f u c k -> fuck)
        for (int i = 0; i < 3; i++) {
            norm = norm.replaceAll("([a-z])\\s+([a-z])", "$1$2");
        }

        // Reduce stretched letters (fuuuuuck -> fuck)
        norm = norm.replaceAll("(\\w)\\1{2,}", "$1");

        return norm;
    }

}
