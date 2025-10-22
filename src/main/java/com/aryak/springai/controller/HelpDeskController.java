package com.aryak.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping(value = "/helpdesk")
public class HelpDeskController {

    private final ChatClient helpDeskChatClient;

    public HelpDeskController(ChatClient helpDeskChatClient) {
        this.helpDeskChatClient = helpDeskChatClient;
    }

    @GetMapping
    public String actsAsAHelpDeskAssistant(@RequestParam String message, @RequestParam String username) {
        return helpDeskChatClient.prompt(message)
                .advisors(s -> s.param(CONVERSATION_ID, username)) // putting conversation id in table
                // embed the username into the tool context
                .toolContext(Map.of("username", username))
                .call()
                .content();
    }
}
