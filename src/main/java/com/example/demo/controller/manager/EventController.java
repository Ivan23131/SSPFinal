package com.example.demo.controller.manager;

import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.service.EventService;
import com.example.demo.service.TicketService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("manager/events/{eventId:\\d+}")
public class EventController {

    private final EventService eventService;
    private final TicketService ticketService;

    @ModelAttribute("event")
    public Event event(@PathVariable("eventId") int eventId) {
        return this.eventService.findEvent(eventId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @ModelAttribute("eventTickets")
    public List<Ticket> ticketList(@PathVariable("eventId") int eventId) {
        return this.ticketService.findTicketsByEvents(eventId);
    }

    @GetMapping
    public String getNewProductPage() {
        return "manager/event/event";
    }

    @GetMapping("edit")
    public String getEventEditPage() {
        return "manager/event/edit";
    }

    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("event") Event event) {
        this.eventService.deleteEvent(event.getId());
        return "redirect:/manager/events/list";
    }

}
