package com.aryak.springai.config;

import com.aryak.springai.tools.HelpDeskTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplates/helpdesk_system_prompt_template.st")
    Resource helpDeskSystemPromptTemplate;

    @Bean("helpDeskChatClient")
    public ChatClient helpDeskChatClient(ChatClient.Builder chatClientBuilder,
                                         ChatMemory chatMemory,
                                         HelpDeskTools helpDeskTools) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                .defaultSystem(helpDeskSystemPromptTemplate)
                .defaultTools(helpDeskTools)
                .defaultAdvisors(List.of(memoryAdvisor))
                .build();
    }
}
