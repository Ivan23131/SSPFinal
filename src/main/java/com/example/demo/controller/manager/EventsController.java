package com.example.demo.controller.manager;

import com.example.demo.controller.manager.payload.FindEventPayload;
import com.example.demo.entity.Event;
import com.example.demo.func.SeatParser;
import com.example.demo.service.EventService;
import com.example.demo.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("manager/events")
public class EventsController {

    private final EventService eventService;

    @GetMapping("list")
    public String getEventsList(Model model, FindEventPayload eventPayload) {
        model.addAttribute("events", this.eventService.findAllCurrentEvent(eventPayload.title()));
        return "manager/event/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {
        return "manager/event/new_event";
    }

    @PostMapping("create")
    public void createProduct(  @RequestParam Map<String, String> newEvent,
                              HttpServletResponse response, @AuthenticationPrincipal User user) throws IOException {
        List<String> listOfErrors = new LinkedList<>();
        LocalDateTime dateTime = null;
        if (newEvent.get("title").length() == 0){
            listOfErrors.add("укажите название");
            newEvent.replace("title","",null);
        }
        if (newEvent.get("place").length() == 0){
            listOfErrors.add("укажите цену билета");
            newEvent.replace("place","",null);
        }
        if (newEvent.get("dateTime").length() == 0){
            listOfErrors.add("укажите время");
            newEvent.replace("dateTime","",null);
        }else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            dateTime = LocalDateTime.parse(newEvent.get("dateTime"), formatter);
        }
        if (newEvent.get("type").length() == 0){
            listOfErrors.add("укажите тип события");
            newEvent.replace("type","",null);
        }
        Integer price;
        try {
            price = Integer.parseInt(newEvent.get("price"));
        }catch (Exception e){
            listOfErrors.add("укажите цену в виде числа");
            price = null;
        }
        if (listOfErrors.size() != 0){
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // Код 400
            // Записываем ошибки в ответ в формате JSON
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("errors", listOfErrors)));
        }else {
            Event event = this.eventService.createEvent(newEvent.get("title"), newEvent.get("place"), newEvent.get("type"), dateTime, user, SeatParser.parseSeats(newEvent),price);
            // Возвращаем перенаправление на новую страницу
            response.setStatus(HttpStatus.FOUND.value()); // Код 302
            response.setHeader("Location", "/manager/events/%d".formatted(event.getId()));
        }
    }
}
