package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Authority;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AuthorityRepository authorityRepository;

    public boolean isUserExists(String username, String passwordHash) {
        return appUserRepository.existsByUsernameOrPasswordHash(username, passwordHash);
    }

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
        appUserRepository.topUpBalance(username, amount);
    }

    public AppUser getUserByUsername(String username) {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("нет такого юзера код смотреть в репозитории AppUser"));
    }
}