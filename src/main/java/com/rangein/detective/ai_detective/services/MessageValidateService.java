package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MessageValidateService {

    public enum MessageViolationType {
        SWEAR_WORDS,
        META_COMMAND,
        MURDER_PHRASE,
        GOD_MODE,
        ESCAPE_PHRASE,
        GENERIC_FORBIDDEN
    }

    public record MessageViolation(
            MessageViolationType type,
            String reason
    ) {}

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
            "bloody", "freak", "freaking", "frig", "frick", "fricking", "shoot", "screw", "heck", "darn", "crud", "butt"
    );

    private static final Map<MessageViolationType, Set<String>> FORBIDDEN_PATTERNS = Map.of(
            MessageViolationType.MURDER_PHRASE, Set.of(
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
            ),
            MessageViolationType.GOD_MODE, Set.of(
                    "i[\\s']?m god",
                    "i am god",
                    "i control( the)? (story|everything|others|fate|world)",
                    "i rewrite( the)? story",
                    "i change( the)? reality",
                    "i control( the)? fate",
                    "i rule( the)? world"
            ),
            MessageViolationType.ESCAPE_PHRASE, Set.of(
                    "she admits",
                    "they run( away)?",
                    "i solved it( myself)?",
                    "end game",
                    "game over"
            ),
            MessageViolationType.META_COMMAND, Set.of(
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
                    "maybe later"
            )
    );

    public Optional<ReplyMessageDto> validateMessage(String message) {
        String normalizedSoft = normalize(message, false);
        String normalizedHard = normalize(message, true);

        for (String word : SWEAR_WORDS) {
            if (normalizedHard.contains(word)) {
                return Optional.of(new ReplyMessageDto(
                        List.of(), List.of(), List.of(), null, true, 0,
                        "Swear words are forbidden"
                ));
            }
        }

        for (var entry : FORBIDDEN_PATTERNS.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (Pattern.compile("\\b" + pattern + "\\b").matcher(normalizedSoft).find()) {
                    return Optional.of(new ReplyMessageDto(
                            List.of(), List.of(), List.of(), null, true, 0,
                            "Message violates rules: " + entry.getKey()
                    ));
                }
            }
        }

        return Optional.empty();
    }

    private String normalize(String message, boolean aggressive) {
        String norm = Normalizer.normalize(message, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Cf}\\p{Cn}]", "")
                .replaceAll("[’‘`´']", "'")
                .replaceAll("[“”]", "\"")
                .replaceAll("\\s*(['\"]+)\\s*", "$1")
                .replaceAll("\\s+", " ")
                .trim();

        if (aggressive) {
            norm = norm
                    .replaceAll("([a-z])\\s+([a-z])", "$1$2") // f u c k -> fuck
                    .replaceAll("([a-z])[-_.,!?;:()\\[\\]{}<>~^|/\\\\]+([a-z])", "$1$2") // f.u.c.k -> fuck
                    .replaceAll("(\\w)\\1{2,}", "$1") // fuuuuck -> fuck
                    .replaceAll("[^a-z0-9'\"\\s]", ""); // drop other garbage

            for (int i = 0; i < 2; i++) {
                norm = norm.replaceAll("([a-z])\\s+([a-z])", "$1$2");
            }

            norm = norm.replaceAll("(\\w)\\1{2,}", "$1");
        }

        return norm;
    }

}
