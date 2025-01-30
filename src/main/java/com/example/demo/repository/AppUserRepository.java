package com.example.demo.repository;

import com.example.demo.entity.AppUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);

    @Modifying
    @Query("UPDATE AppUser u SET u.balance = u.balance + :amount WHERE u.username = :username")
    void topUpBalance(@Param("username") String username, @Param("amount") Integer amount);

    @Modifying
    @Query("UPDATE AppUser u SET u.balance = u.balance - :amount WHERE u.username = :username")
    void decreaseBalanceByUsername(@Param("username") String username, @Param("amount") Integer amount);

    @Query("SELECT SUM(t.price) FROM Ticket t " +
            "JOIN t.client u " +
            "JOIN t.event e " +
            "WHERE u.username = :username AND e.dateTime < :currentDate")
    Optional<Integer> calculateUserSpending(@Param("username") String username,
                                  @Param("currentDate") LocalDateTime currentDate);

    boolean existsByUsernameOrPasswordHash(String username, String passwordHash);
}
