package com.example.demo.controller.manager.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record NewEventPayload(String title, String place, String type, @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime, List<NewTicketPayload> ticketList, Integer price, Integer organizer_id) {
}