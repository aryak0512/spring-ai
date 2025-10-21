package com.aryak.springai.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class TokenCostAuditAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        // propagate the call
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        assert chatClientResponse.chatResponse() != null;
        var usage = chatClientResponse.chatResponse().getMetadata().getUsage();
        log.info("Usage : {}", usage);
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TOKEN AUDIT ADVISOR";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
