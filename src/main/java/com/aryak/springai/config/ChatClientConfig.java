package com.aryak.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
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

    @Bean
    public ChatClient openAiClient(OpenAiChatModel openAiChatModel) {
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                         You are a java developer and a techie. You only answer questions\s
                         related to technology and coding. For any other questions asked you
                         reply that you cannot help answer it.
                        \s""");
        return builder.build();
    }
}
