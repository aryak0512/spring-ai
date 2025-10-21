package com.aryak.springai.config;

import com.aryak.springai.advisors.TokenCostAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

    @Bean
    public ChatClient basicClient(OpenAiChatModel openAiChatModel) {
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor());
        return builder.build();
    }

    @Bean
    public ChatClient chatMemoryClient(OpenAiChatModel openAiChatModel,
                                       JdbcChatMemoryRepository jdbcChatMemoryRepository) {

        // customise maxMessages
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(200)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();

        // configure the memory chat advisor
        Advisor memoryChatAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(memoryChatAdvisor);
        return builder.build();
    }

    /**
     * This is required for vector store since qdrant
     * gets an error when both LLM jars are on classpath
     *
     * @param openAiEmbeddingModel
     * @return
     */
    @Bean
    @Primary
    public EmbeddingModel embeddingModel(OpenAiEmbeddingModel openAiEmbeddingModel) {
        return openAiEmbeddingModel;
    }

    @Bean
    public ChatClient ragClient(OpenAiChatModel openAiChatModel,
                                JdbcChatMemoryRepository jdbcChatMemoryRepository,
                                RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {

        // customise maxMessages
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(200)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();

        // configure the memory chat advisor
        Advisor memoryChatAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        ChatClient.Builder builder = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(memoryChatAdvisor, retrievalAugmentationAdvisor);
        return builder.build();
    }

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(final VectorStore vectorStore,
                                                              final OpenAiChatModel openAiChatModel) {

        var documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.7)
                .build();

        // to handle prompts other than english
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer
                .builder()
                .chatClientBuilder(ChatClient.builder(openAiChatModel))
                .targetLanguage("english")
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(translationQueryTransformer)
                .documentRetriever(documentRetriever)
                .build();
    }
}
