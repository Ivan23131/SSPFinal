package com.example.demo.controller.client.credit;


import com.example.demo.controller.client.credit.payload.CreditPayload;
import com.example.demo.entity.AppUser;
import com.example.demo.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("client")
public class CreditController {

    private final AppUserService appUserService;

    @GetMapping("credit")
    public String getCreditPage(@AuthenticationPrincipal User user, Model model){
        AppUser client = appUserService.getUserByUsername(user.getUsername());
        if (appUserService.isUserSuperclientIfNotGetAuthority(user.getUsername())){
            model.addAttribute("balance", client.getBalance());
            model.addAttribute("credit", client.getCredit());
            return"client/credit/credit";
        }
        return "client/credit/not_superclient";
    }

    @PostMapping("credit")
    public String getCredit(Model model, CreditPayload payload, @AuthenticationPrincipal User user) {
        try {
            Integer balance = Integer.parseInt(payload.balance());
            if (balance < 0){
                throw new Exception();
            }
            appUserService.depositBalance(user.getUsername(), balance);
            appUserService.increaseCredit(user.getUsername(), balance);
        }catch (Exception e){
            model.addAttribute("error","Укажите цену");
            model.addAttribute("balance", appUserService.getUserByUsername(user.getUsername()).getBalance());
            model.addAttribute("credit", appUserService.getUserByUsername(user.getUsername()).getCredit());
            return "client/credit/credit";
        }
        return "redirect:/user/start";
    }

    @PostMapping("credit/repay")
    public String getRepay(Model model, CreditPayload payload, @AuthenticationPrincipal User user) {
        try {
            Integer balance = Integer.parseInt(payload.balance());
            if (balance < 0 || appUserService.getUserByUsername(user.getUsername()).getCredit() < balance){
                model.addAttribute("error1","Укажите цену которая будет меньше");
                throw new Exception();
            }
            appUserService.depositBalance(user.getUsername(), balance*(-1));
            appUserService.increaseCredit(user.getUsername(), balance*(-1));
        }catch (Exception e){
            model.addAttribute("error","Укажите цену");
            model.addAttribute("balance", appUserService.getUserByUsername(user.getUsername()).getBalance());
            model.addAttribute("credit", appUserService.getUserByUsername(user.getUsername()).getCredit());
            return "client/credit/credit";
        }
        return "redirect:/user/start";
    }
}
