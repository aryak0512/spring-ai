package com.aryak.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;
    private final ChatClient openAiClient;

    @Value("classpath:/promptTemplates/user_prompt_template.st")
    private Resource userPromptTemplate;

    @Value("classpath:/promptTemplates/system_prompt_template.st")
    private Resource systemPromptTemplate;

    @Value("classpath:/promptTemplates/hr_system_prompt_template.st")
    private Resource hrSystemPromptTemplate;

    public ChatController(ChatClient ollamaClient, ChatClient openAiClient) {
        this.ollamaClient = ollamaClient;
        this.openAiClient = openAiClient;
    }

    @GetMapping(value = "/ollama")
    public String testPrompt(@RequestParam String message) {
        return ollamaClient.prompt(message).call().content();
    }

    @GetMapping(value = "/openai")
    public String testPrompt2(@RequestParam String message) {
        return openAiClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/email")
    public String emailResponse(@RequestParam("customerName") String customerName,
                                @RequestParam("customerMessage") String customerMessage) {
        return openAiClient
                .prompt()
                .system(systemPromptTemplate) // overriding default system message from the one in config
                .user(promptTemplateSpec ->
                        promptTemplateSpec.text(userPromptTemplate)
                                .param("customerName", customerName)
                                .param("customerMessage", customerMessage))
                .call().content();
    }

    @GetMapping("/promptStuffing")
    public String promptStuffing(@RequestParam String message) {
        return openAiClient
                .prompt()
                .system(hrSystemPromptTemplate)
                .user(message)
                .call().content();
    }
}
