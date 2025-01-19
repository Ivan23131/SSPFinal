package com.example.demo.func;

import com.example.demo.controller.client.payload.ParsedTicket;
import com.example.demo.controller.client.payload.UpdateTickets;
import com.example.demo.controller.manager.payload.NewTicketPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SeatParser {

    public static List<NewTicketPayload> parseSeats(Map<String, String> data) {
        List<NewTicketPayload> seats = new ArrayList<>();

        // Проходим по всем ключам в Map
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Проверяем, соответствует ли ключ формату "seats[index]"
            if (key.startsWith("seats[")) {
                // Извлекаем индекс из ключа
                int index = Integer.parseInt(key.replace("seats[", "").replace("]", ""));

                // Разделяем значение на числа
                String[] numbers = value.split(",");
                List<Integer> seatNumbers = new ArrayList<>();
                for (String number : numbers) {
                    seatNumbers.add(Integer.parseInt(number));
                }

                // Добавляем числа в список по индексу
                seats.add(new NewTicketPayload(seatNumbers.get(0), seatNumbers.get(1)));
            }
        }

        return seats;
    }
    public static List<ParsedTicket> parseTickets(List<UpdateTickets> updateTickets) {
        return updateTickets.stream()
                .map(ticket -> {
                    String[] parts = ticket.id().split("-"); // Разделяем id на части
                    int row = Integer.parseInt(parts[1]); // Извлекаем ряд
                    int place = Integer.parseInt(parts[2]); // Извлекаем место
                    String status = ticket.status(); // Получаем статус
                    return new ParsedTicket(row, place, status); // Создаем новый ParsedTicket
                })
                .collect(Collectors.toList()); // Собираем результат в список
    }

    public static void main(String[] args) {
        List<UpdateTickets> updateTickets = List.of(
                new UpdateTickets("seat-2-3", "забронирован"),
                new UpdateTickets("seat-3-2", "продан")
        );

        List<ParsedTicket> parsedTickets = parseTickets(updateTickets);

        // Вывод результата для проверки
        parsedTickets.forEach(ticket -> System.out.println(
                "Row: " + ticket.getRow() +
                        ", Place: " + ticket.getPlace() +
                        ", Status: " + ticket.getStatus()
        ));
    }
}