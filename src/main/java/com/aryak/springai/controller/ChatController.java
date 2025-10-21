package com.aryak.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;
    private final ChatClient openAiClient;

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
                .system("""
                         You are a java developer and a techie. You only answer questions\s
                         related to technology and coding. For any other questions asked you
                         reply that you cannot help answer it.
                        \s""")
                .user(message)
                .call()
                .content();
    }
}
