package com.example.demo.repository;

import com.example.demo.entity.Authority;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, Integer> {
    //    Поиск роли по названию
    Authority findByAuthority(String authority);
}