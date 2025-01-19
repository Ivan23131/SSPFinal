package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends CrudRepository<Event, Integer> {

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :filter, '%')) AND e.dateTime > :currentDateTime")
    Iterable<Event> findAllByTitleLikeIgnoreCase(@Param("filter") String filter, @Param("currentDateTime") LocalDateTime currentDateTime);


    @Query("SELECT e FROM Event e WHERE e.dateTime > :currentDateTime")
    Iterable<Event> findAllWithDateTimeAfter(@Param("currentDateTime") LocalDateTime currentDateTime);
}
