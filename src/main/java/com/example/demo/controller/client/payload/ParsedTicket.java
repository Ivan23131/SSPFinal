package com.example.demo.controller.client.payload;


import lombok.Data;

@Data
public class ParsedTicket {
    int row;
    int place;
    String status;

    public ParsedTicket(int row, int place, String status) {
        this.row = row;
        this.place = place;
        this.status = status;
    }
}
