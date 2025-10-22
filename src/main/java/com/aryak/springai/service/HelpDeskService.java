package com.aryak.springai.service;

import com.aryak.springai.entity.HelpDeskTicket;
import com.aryak.springai.model.TicketRequest;
import com.aryak.springai.repository.HelpDeskTicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HelpDeskService {

    private final HelpDeskTicketRepository helpDeskTicketRepository;

    public HelpDeskService(HelpDeskTicketRepository helpDeskTicketRepository) {
        this.helpDeskTicketRepository = helpDeskTicketRepository;
    }

    public HelpDeskTicket createTicket(TicketRequest ticketInput, String username) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketInput.issue())
                .username(username)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        helpDeskTicketRepository.save(ticket);
        return ticket;
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }

}
