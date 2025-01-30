package com.example.demo.service;

import com.example.demo.controller.client.payload.ParsedTicket;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final EntityManager entityManager;
    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;

    @Transactional
    public void updateBookedTicketsByEventId(Integer eventId){
        for (Ticket ticket: findTicketsByEvents(eventId)){
            if (ticket.getStatus().equals("забронирован")){
                ticket.setStatus("продается");
                ticket.setClient(null);
                ticketRepository.save(ticket);
            }
        }
    }

    @Transactional
    public List<Ticket> findTicketsByEvents(Integer eventId) {
        return StreamSupport.stream(ticketRepository.findTicketsByEventId(eventId).spliterator(), false).toList();
    }

    @Transactional
    public void cancelTicket(Integer ticketId, String username){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("нет такого билета по данному id"));
        if (ticket.getStatus().equals("продан")){
            AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера с такм по id функция удаления билета"));
            client.setBalance(client.getBalance() + ticket.getPrice());
            appUserRepository.save(client);
        }
        ticket.setClient(null);
        ticket.setStatus("продается");
        ticketRepository.save(ticket);
    }

    @Transactional
    public void buyBookedTicket(Integer ticketId, String username){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("нет такого билета по данному id"));
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера с такм по id функция удаления билета"));
        client.setBalance(client.getBalance() - ticket.getPrice());
        appUserRepository.save(client);
        ticket.setStatus("продан");
        ticketRepository.save(ticket);
    }


    @Transactional
    public boolean IsTicketsWhereUpdated(String username, List<ParsedTicket> updateTickets, Integer eventId){
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера по имени"));

        boolean isTicketsWhereUpdated = false;
        for (ParsedTicket ticket: updateTickets){
            if (!ticketRepository.findByEventIdAndRowAndPlace(eventId, ticket.getRow(), ticket.getPlace()).orElseThrow(() -> new RuntimeException("в проверке билетов что их не успели поменять нет такого билета")).getStatus().equals("продается")){
                isTicketsWhereUpdated = true;
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        for (ParsedTicket ticket: updateTickets){
            Ticket ticket1 = this.ticketRepository.findByEventIdAndRowAndPlace(eventId, ticket.getRow(), ticket.getPlace()).orElseThrow(() -> new RuntimeException("нет такого биелта по ивенту и месту"));
            ticket1.setStatus(ticket.getStatus());
            ticket1.setClient(client);
            ticketRepository.save(ticket1);
        }
        return isTicketsWhereUpdated;
    }

    @Transactional
    public List<Ticket> getActiveTicketsByUsername(String username) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return ticketRepository.findActiveTicketsByUsername(username, currentDateTime);
    }
}
