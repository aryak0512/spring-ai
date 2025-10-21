package com.aryak.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
public class TimeController {
    private final ChatClient timeChatClient;

    public TimeController(ChatClient timeChatClient) {
        this.timeChatClient = timeChatClient;
    }

    @GetMapping("/local-time")
    public ResponseEntity<String> localTime(@RequestParam("username") String username,
                                            @RequestParam("message") String message) {
        String answer = timeChatClient.prompt()
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }
}
