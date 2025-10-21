package com.aryak.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;

    public ChatController(ChatClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @GetMapping(value = "/ollama")
    public String testPrompt(@RequestParam String message) {
        return ollamaClient.prompt(message).call().content();
    }
}
