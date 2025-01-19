package com.example.demo.controller.client.tickets;


import com.example.demo.entity.AppUser;
import com.example.demo.service.AppUserService;
import com.example.demo.service.EventService;
import com.example.demo.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        return "client/ticket/ticket_list";
    }

    @PostMapping("tickets/buy-ticket")
    public String buyTicket(@RequestParam("price") Integer ticketPrice, @RequestParam("ticketId") String status, @RequestParam("ticketId") Integer ticketId, @AuthenticationPrincipal User user, Model model){
        AppUser client = userService.getUserByUsername(user.getUsername());
        if (ticketPrice > userService.getUserByUsername(user.getUsername()).getBalance()){
            System.out.println("qecwecwec");
            model.addAttribute("error","не хватает баланса");
            model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
            model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
            return "client/ticket/ticket_list";
        }
        System.out.println("wecwecwe");
        ticketService.buyBookedTicket(ticketId, user.getUsername());
        model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        return "client/ticket/ticket_list";
    }


    @PostMapping("tickets/cancel")
    public String buyBookedTicket(@RequestParam("ticketId") Integer ticketId, @AuthenticationPrincipal User user, Model model){
        AppUser client = userService.getUserByUsername(user.getUsername());
        ticketService.cancelTicket(ticketId, user.getUsername());
        model.addAttribute("ticketList", ticketService.getActiveTicketsByUsername(client.getUsername()));
        return "client/ticket/ticket_list";
    }
}
