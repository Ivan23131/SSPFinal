package com.example.demo.service;

import com.example.demo.controller.manager.payload.NewTicketPayload;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;

    public Iterable<Event> findAllCurrentEvent(String filter) {
        LocalDateTime now = LocalDateTime.now();
        if (filter != null && !filter.isBlank()) {
            return this.eventRepository.findAllByTitleLikeIgnoreCase(filter, now);
        } else {
            return this.eventRepository.findAllWithDateTimeAfter(now);
        }
    }

    @Transactional
    public Event createEvent(String title, String place, String type, LocalDateTime dateTime, User user, List<NewTicketPayload> tickets, Integer ticketPrice) {
        AppUser selmagUser = appUserRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getUsername()));
        Event event = this.eventRepository.save(new Event(null, title, place, type, dateTime, selmagUser));
        for (NewTicketPayload ticket: tickets){
            this.ticketRepository.save(new Ticket(null, event, "продается" ,ticket.row(), ticket.place(), ticketPrice, null));
        }
        return event;
    }

    public Optional<Event> findEvent(int eventId) {
        return this.eventRepository.findById(eventId);
    }

    @Transactional
    public void deleteEvent(Integer id) {
        this.eventRepository.deleteById(id);
    }
}
