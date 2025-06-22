package com.rangein.detective.ai_detective.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtil {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}