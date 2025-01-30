package com.example.demo.controller.client;


import com.example.demo.controller.client.payload.ParsedTicket;
import com.example.demo.controller.client.payload.UpdateTickets;
import com.example.demo.controller.client.payload.UpdateTicketsTotalPrice;
import com.example.demo.controller.manager.payload.FindEventPayload;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.service.AppUserService;
import com.example.demo.service.EventService;
import com.example.demo.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.example.demo.func.SeatParser.parseTickets;


@AllArgsConstructor
@RequestMapping("client")
@Controller
public class ClientController {

    private final EventService eventService;
    private final AppUserService userService;
    private final TicketService ticketService;


    @GetMapping("events/list")
    public String getEventsList(Model model, FindEventPayload eventPayload, @AuthenticationPrincipal User user) {
        model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
        model.addAttribute("events", this.eventService.findAllCurrentEvent(eventPayload.title()));
        return "client/event/list_events";
    }

    @GetMapping("events/{eventId:\\d+}")
    public String getNewOrderPage(HttpServletResponse response, Model model, @PathVariable("eventId") int eventId, @AuthenticationPrincipal User user) {
        Cookie cookie = new Cookie("event_" + eventId, java.lang.String.valueOf(eventId));//создаем объект Cookie,
        //в конструкторе указываем значения для name и value
        cookie.setPath("/");//устанавливаем путь
        cookie.setMaxAge(30);//здесь устанавливается время жизни куки
        response.addCookie(cookie);//добавляем Cookie в запрос
        response.setContentType("text/plain");//устанавливаем контекст
        List<Ticket> ticketList = StreamSupport.stream(this.ticketService.findTicketsByEvents(eventId).spliterator(), false).toList();
        Event event = this.eventService.findEvent(eventId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
        if (Duration.between(LocalDateTime.now(), event.getDateTime()).toHours() < 24){
            model.addAttribute("timeEventFlag", 1);
            ticketService.updateBookedTicketsByEventId(eventId);
        }else {
            model.addAttribute("timeEventFlag", 0);
        }
        model.addAttribute("event", event);
        model.addAttribute("eventId", eventId);
        model.addAttribute("eventTickets", ticketList);
        return "client/event/event";
    }

    @GetMapping("events/previous")
    public String getPreviousEvents(HttpServletRequest request, Model model){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "client/event/previous";
        }
        // Фильтруем куки, которые относятся к событиям (начинаются с "event_")
        List<Event> events = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (cookie.getName().startsWith("event_")) {
                events.add(eventService.findEvent(Integer.parseInt(cookie.getValue())).orElseThrow(() -> new RuntimeException("нет такого ивента с такм айд в контроллере клиента при получении куков")));
            }
        }
        model.addAttribute("events", events);
        return "client/event/previous";
    }

    @PostMapping("events/{eventId:\\d+}")
    public void getUpdateEvent(HttpServletResponse response, @PathVariable("eventId") int eventId, @RequestBody UpdateTicketsTotalPrice payload, Model model, @AuthenticationPrincipal User user) throws IOException {
        AppUser client = userService.getUserByUsername(user.getUsername());
        List<String> listOfErrors = new LinkedList<>();
        List<ParsedTicket> parsedTickets = parseTickets(payload.selectedSeats());
        if (client.getBalance() < payload.totalPrice()){
            List<Ticket> ticketList = StreamSupport.stream(this.ticketService.findTicketsByEvents(eventId).spliterator(), false).toList();
            Event event = this.eventService.findEvent(eventId)
                    .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
            model.addAttribute("event", event);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventTickets", ticketList);
            model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
            listOfErrors.add("недостаточно денег");
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // Код 400
            // Записываем ошибки в ответ в формате JSON
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("errors", listOfErrors)));
        }else if (this.ticketService.IsTicketsWhereUpdated(user.getUsername(), parsedTickets, eventId)){
            List<Ticket> ticketList = StreamSupport.stream(this.ticketService.findTicketsByEvents(eventId).spliterator(), false).toList();
            Event event = this.eventService.findEvent(eventId)
                    .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
            model.addAttribute("event", event);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventTickets", ticketList);
            model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
            listOfErrors.add("эти билеты были выкуплены или забронированы");
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // Код 400
            // Записываем ошибки в ответ в формате JSON
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("errors", listOfErrors)));
        }
        else {
            userService.decreaseBalanceByUsername(client.getUsername(), payload.totalPrice());
        }
    }
}