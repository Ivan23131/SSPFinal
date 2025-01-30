package com.example.demo.controller.client.tickets;


import com.example.demo.entity.AppUser;
import com.example.demo.entity.Event;
import com.example.demo.service.AppUserService;
import com.example.demo.service.EventService;
import com.example.demo.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequestMapping("client")
@Controller
public class TikcketClientController {

    private final EventService eventService;
    private final AppUserService userService;
    private final TicketService ticketService;

    @GetMapping("tickets/list")
    public String getActiveClientTickets(@AuthenticationPrincipal User user, Model model){
        AppUser client = userService.getUserByUsername(user.getUsername());
        for (Event event:eventService.findAllCurrentEvent("")){
            if (Duration.between(LocalDateTime.now(), event.getDateTime()).toHours() < 24) {
                ticketService.updateBookedTicketsByEventId(event.getId());
            }
        }
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        model.addAttribute("balance", client.getBalance());
        return "client/ticket/ticket_list";
    }

    @PostMapping("tickets/buy-ticket")
    public String buyTicket(@RequestParam("price") Integer ticketPrice, @RequestParam("ticketId") String status, @RequestParam("ticketId") Integer ticketId, @AuthenticationPrincipal User user, Model model){
        AppUser client = userService.getUserByUsername(user.getUsername());
        if (ticketPrice > userService.getUserByUsername(user.getUsername()).getBalance()){
            model.addAttribute("error","не хватает баланса");
            model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
            model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
            return "client/ticket/ticket_list";
        }
        ticketService.buyBookedTicket(ticketId, user.getUsername());
        model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        return "client/ticket/ticket_list";
    }


    @PostMapping("tickets/cancel")
    public String buyBookedTicket(@RequestParam("ticketId") Integer ticketId, @AuthenticationPrincipal User user, Model model){
        AppUser client = userService.getUserByUsername(user.getUsername());
        ticketService.cancelTicket(ticketId, user.getUsername());
        model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        return "client/ticket/ticket_list";
    }
}
