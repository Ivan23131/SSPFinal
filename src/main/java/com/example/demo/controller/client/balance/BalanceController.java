package com.example.demo.controller.client.balance;


import com.example.demo.controller.client.balance.balance.BalancePayload;
import com.example.demo.controller.manager.payload.FindEventPayload;
import com.example.demo.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("client")
public class BalanceController {

    private final AppUserService userService;

    @GetMapping("balance")
    public String getEventsList(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
        return "client/balance/balance";
    }

    @PostMapping("balance")
    public String getEventsList(Model model, BalancePayload payload, @AuthenticationPrincipal User user) {
        try {
            Integer balance = Integer.parseInt(payload.balance());
            if (balance < 0){
                throw new Exception();
            }
            userService.depositBalance(user.getUsername(), balance);
        }catch (Exception e){
            model.addAttribute("error","Укажите цену");
            model.addAttribute("balance", userService.getUserByUsername(user.getUsername()).getBalance());
            return "client/balance/balance";
        }
        return "redirect:/user/start";
    }
}
