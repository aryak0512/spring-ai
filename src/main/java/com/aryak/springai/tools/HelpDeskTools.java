package com.aryak.springai.tools;

import com.aryak.springai.entity.HelpDeskTicket;
import com.aryak.springai.model.TicketRequest;
import com.aryak.springai.service.HelpDeskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HelpDeskTools {

    private final HelpDeskService helpDeskService;

    public HelpDeskTools(HelpDeskService helpDeskService) {
        this.helpDeskService = helpDeskService;
    }

    @Tool(name = "createTicket", description = "Create the Support Ticket", returnDirect = true)
    String createTicket(@ToolParam(description = "Details to create a Support ticket")
                        TicketRequest ticketRequest, ToolContext toolContext) {
        String username = getUsernameFromContext(toolContext);
        log.info("Creating support ticket for user: {} with details: {}", username, ticketRequest);
        HelpDeskTicket savedTicket = helpDeskService.createTicket(ticketRequest, username);
        log.info("Ticket created successfully. Ticket ID: {}, Username: {}", savedTicket.getId(), savedTicket.getUsername());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = getUsernameFromContext(toolContext);
        List<HelpDeskTicket> tickets = helpDeskService.getTicketsByUsername(username);
        log.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }

    private String getUsernameFromContext(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        if (username == null) {
            throw new RuntimeException("Missing username in tool context");
        }
        return username;
    }
}
