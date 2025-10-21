package com.aryak.springai.controller;

import com.aryak.springai.model.CountryCitiesResponseDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;
    private final ChatClient openAiClient;
    private final ChatClient basicClient;
    private final ChatClient chatMemoryClient;

    @Value("classpath:/promptTemplates/user_prompt_template.st")
    private Resource userPromptTemplate;

    @Value("classpath:/promptTemplates/system_prompt_template.st")
    private Resource systemPromptTemplate;

    @Value("classpath:/promptTemplates/hr_system_prompt_template.st")
    private Resource hrSystemPromptTemplate;

    public ChatController(ChatClient ollamaClient,
                          ChatClient openAiClient,
                          ChatClient basicClient, ChatClient chatMemoryClient) {
        this.ollamaClient = ollamaClient;
        this.openAiClient = openAiClient;
        this.basicClient = basicClient;
        this.chatMemoryClient = chatMemoryClient;
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

    @GetMapping(value = "/advisors")
    public String exploreDefaultAdvisors(@RequestParam String message) {
        return openAiClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping(value = "/stream")
    public Flux<String> exploreStreaming(@RequestParam String message) {
        return basicClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    @GetMapping(value = "/structured")
    public CountryCitiesResponseDto exploreStructured(@RequestParam String message) {
        return basicClient.prompt()
                .user(message)
                .call()
                .entity(CountryCitiesResponseDto.class);
    }

    @GetMapping(value = "/list")
    public List<String> exploreListOutputConverter(@RequestParam String message) {
        return basicClient.prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());
    }

    @GetMapping(value = "/map")
    public Map<String, Object> exploreMapOutputConverter(@RequestParam String message) {
        return basicClient.prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());
    }

    @GetMapping(value = "/memory")
    public String exploreMapOutputConverter(@RequestParam String message,
                                            @RequestParam String username) {
        return chatMemoryClient.prompt()
                .user(message)
                .advisors(s -> {
                    s.param(CONVERSATION_ID, username);
                    s.param("region", "India");
                })
                .call()
                .content();
    }
}
