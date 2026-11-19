package com.SpringAI.ChatBot.config;


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.embedding.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(
            @Value("${spring.ai.google.genai.embedding.api-key}") String apiKey) {

        var connectionDetails = GoogleGenAiEmbeddingConnectionDetails.builder()
                .apiKey(apiKey)
                .build();

        var options = GoogleGenAiTextEmbeddingOptions.builder()
                .model("gemini-embedding-001")
                .build();

        return new GoogleGenAiTextEmbeddingModel(connectionDetails, options);
    }
}
