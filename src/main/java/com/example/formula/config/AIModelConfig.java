package com.example.formula.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIModelConfig {
    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${app.ai.openai-api-key}") String apiKey,
            @Value("${app.ai.model-name}") String modelName,
            @Value("${app.ai.temperature}") Double temperature) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }
}
