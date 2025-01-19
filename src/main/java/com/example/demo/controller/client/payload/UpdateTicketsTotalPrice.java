package com.example.demo.controller.client.payload;

import java.util.List;

public record UpdateTicketsTotalPrice (List<UpdateTickets> selectedSeats, Integer totalPrice){
}
