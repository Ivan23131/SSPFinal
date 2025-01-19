package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_title")
    @NotNull
    @Size(min = 3, max = 50)
    private String title;

    @Column(name = "c_place")
    @NotNull
    @Size(max = 1000)
    private String place;

    @Column(name = "c_type")
    @NotNull
    @Size(max = 100)
    private String type;

    @Column(name = "c_date")
    @NotNull
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "c_organizer_id", nullable = false)
    private AppUser selmagUser;
}