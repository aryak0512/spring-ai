package com.aryak.springai.config;

import com.aryak.springai.advisors.TokenCostAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

        var chatOptions = ChatOptions.builder()
                .temperature(0.8)
                //.model("")
                .maxTokens(25)
                .build();

        List<Advisor> advisors = List.of(
                new SimpleLoggerAdvisor(),
                new SafeGuardAdvisor(List.of("password")),
                new TokenCostAuditAdvisor()
        );

        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultOptions(chatOptions)
                .defaultAdvisors(advisors)
                .defaultSystem("""
                         You are a java developer and a techie. You only answer questions\s
                         related to technology and coding. For any other questions asked you
                         reply that you cannot help answer it.
                        \s""");
        return builder.build();
    }
}
