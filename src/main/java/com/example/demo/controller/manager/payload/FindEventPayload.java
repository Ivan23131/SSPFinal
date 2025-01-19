package com.example.demo.controller.manager.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record FindEventPayload (String title, String place, String type, @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime){}
