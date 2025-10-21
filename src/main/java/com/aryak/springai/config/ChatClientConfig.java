package com.aryak.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * config for creating the chat client for a multimodel project
 */
@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient ollamaClient(OllamaChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        return builder.build();
    }
}
