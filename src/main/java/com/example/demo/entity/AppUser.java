package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user") // Указываем имя таблицы в базе данных
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50) // Уникальное имя пользователя
    private String username;

    @Column(name = "password_hash", nullable = false) // Хэш пароля
    private String passwordHash;

    @Column(name = "c_balance", nullable = false)
    private Integer balance;


    @Column(name = "c_credit")
    private Integer credit;

    @ManyToMany
    @JoinTable(name = "t_user_authority",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_authority"))
    private List<Authority> authorities = new LinkedList<>();

}