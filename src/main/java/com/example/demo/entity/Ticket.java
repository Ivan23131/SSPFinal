package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "c_status")
    private String status;

    @Column(name = "c_row")
    private Integer row;

    @Column(name = "c_place")
    private Integer place;

    @Column(name = "c_price")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "c_user")
    private AppUser client;
}