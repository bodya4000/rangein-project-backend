package com.rangein.detective.ai_detective.configs;

import com.google.genai.Client;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.rangein.detective.ai_detective.utils.EnvUtil.getEnv;


@Configuration
public class AiConfig {

    @Bean
    public OpenAIClient openAIClient() {
        String API_KEY = getEnv("OPEN_API_KEY");
        return OpenAIOkHttpClient.builder()
                .apiKey(API_KEY)
                .build();
    }

    @Bean
    public Client geminiClient() {
        String API_KEY = getEnv("GEMINI_API_KEY");
        return Client.builder().apiKey(API_KEY).build();
    }
}


