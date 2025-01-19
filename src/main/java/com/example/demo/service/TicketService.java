package com.example.demo.service;

import com.example.demo.controller.client.payload.ParsedTicket;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;

    public Optional<Ticket> findTicket(Integer id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findTicketsByEvents(Integer eventId) {
        return StreamSupport.stream(ticketRepository.findTicketsByEventId(eventId).spliterator(), false).toList();
    }

    @Transactional
    public Ticket createTicket(Event event, int row, int place, int price) {
        Ticket ticket = new Ticket(null, event, "продается", row, place, price, null);
        return ticketRepository.save(ticket);
    }


    @Transactional
    public void updateTickets(String username, List<ParsedTicket> updateTickets, Integer eventId){
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера по имени"));
        for (ParsedTicket ticket: updateTickets){
            Ticket ticket1 = this.ticketRepository.findByEventIdAndRowAndPlace(eventId, ticket.getRow(), ticket.getPlace()).orElseThrow(() -> new RuntimeException("нет такого биелта по ивенту и месту"));
            ticket1.setStatus(ticket.getStatus());
            ticket1.setClient(client);
            ticketRepository.save(ticket1);
        }
    }


    @Transactional
    public List<Ticket> getActiveTicketsByUsername(String username) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return ticketRepository.findActiveTicketsByUsername(username, currentDateTime);
    }
}
