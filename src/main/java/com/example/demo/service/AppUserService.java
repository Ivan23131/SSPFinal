package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Authority;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class AppUserService {

    private final Integer SUPERCLIENT_EXPENSE = 10_000;
    private final AppUserRepository appUserRepository;
    private final AuthorityRepository authorityRepository;


    @Transactional
    public boolean isUserExists(String username, String passwordHash) {
        return appUserRepository.existsByUsernameOrPasswordHash(username, passwordHash);
    }

    @Transactional
    public void increaseCredit(String username, Integer amount) {
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера с таким именем в функции добавления кредита"));
        client.setCredit(client.getCredit() + amount);
        appUserRepository.save(client);
    }

    @Transactional
    public boolean isUserSuperclientIfNotGetAuthority(String username) {
        boolean isUserSuperclient = false;
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера для функции проверки юзера на сурерюезра"));
        if (client.getAuthorities().contains(authorityRepository.findByAuthority("superclient"))){
            isUserSuperclient = true;
        }else if (appUserRepository.calculateUserSpending(username, LocalDateTime.now()).orElse(0) >= SUPERCLIENT_EXPENSE){
            client.getAuthorities().add(authorityRepository.findByAuthority("superclient"));
            appUserRepository.save(client);
            isUserSuperclient = true;
        }
        return isUserSuperclient;
    }

    @Transactional
    public Authority findAuthority(String role) {
        // Ищем роль в базе данных
        Authority authority = authorityRepository.findByAuthority(role);
        return authority;
    }


    @Transactional
    public void saveUser(AppUser user) {
        appUserRepository.save(user);
    }

    @Transactional
    public void decreaseBalanceByUsername(String username, Integer amount){
        appUserRepository.decreaseBalanceByUsername(username, amount);
    }

    @Transactional
    public void depositBalance(String username, Integer amount){
        AppUser client = appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого клиента с таким именем в функции добаыления баланса"));
        client.setBalance(client.getBalance() + amount);
        appUserRepository.save(client);
    }

    @Transactional
    public AppUser getUserByUsername(String username) {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера код смотреть в репозитории AppUser"));
    }
}