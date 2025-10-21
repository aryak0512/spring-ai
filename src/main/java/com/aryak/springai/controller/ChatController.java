package com.aryak.springai.controller;

import com.aryak.springai.model.CountryCitiesResponseDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;
    private final ChatClient openAiClient;
    private final ChatClient basicClient;
    private final ChatClient chatMemoryClient;
    private final ChatClient ragClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplates/user_prompt_template.st")
    private Resource userPromptTemplate;

    @Value("classpath:/promptTemplates/system_prompt_template.st")
    private Resource systemPromptTemplate;

    @Value("classpath:/promptTemplates/hr_system_prompt_template.st")
    private Resource hrSystemPromptTemplate;

    @Value("classpath:/promptTemplates/random_data_prompt_template.st")
    private Resource randomDataPromptTemplate;

    @Value("classpath:/promptTemplates/hr_rag_prompt_template.st")
    private Resource hrRagPromptTemplate;

    public ChatController(ChatClient ollamaClient,
                          ChatClient openAiClient,
                          ChatClient basicClient, ChatClient chatMemoryClient, ChatClient ragClient, VectorStore vectorStore) {
        this.ollamaClient = ollamaClient;
        this.openAiClient = openAiClient;
        this.basicClient = basicClient;
        this.chatMemoryClient = chatMemoryClient;
        this.ragClient = ragClient;
        this.vectorStore = vectorStore;
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

    @GetMapping("/rag")
    public String ragResponse(@RequestParam("message") String message) {

        // fetching relevant sentences on the topic from vector store
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(3) // select top 3 results in relevancy
                .query(message)
                .similarityThreshold(0.7) // match objects that are more than 70% relevant
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        System.out.println("No. of docs picked from vector store :" + similarDocuments.size());

        String documents = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(","));
        System.out.println("Sentences merged into a single string as :" + documents);

        return ragClient
                .prompt()
                .system(promptTemplateSpec ->
                        promptTemplateSpec.text(hrRagPromptTemplate)
                                .param("documents", documents))
                .user(message)
                .call().content();
    }
}
