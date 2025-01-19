package com.example.demo.repository;

import com.example.demo.entity.Ticket;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Integer> {

    Ticket findById(int id);

    Iterable<Ticket> findTicketsByEventId(Integer eventId);

    Optional<Ticket> findByEventIdAndRowAndPlace(Integer eventId, Integer row, Integer place);

    @Query("SELECT t FROM Ticket t " +
            "JOIN t.event e " +
            "JOIN t.client u " +
            "WHERE u.username = :username AND e.dateTime > :currentDateTime")
    List<Ticket> findActiveTicketsByUsername(@Param("username") String username,
                                             @Param("currentDateTime") LocalDateTime currentDateTime);



    @Modifying
    @Query("UPDATE Ticket t SET t.status = :status WHERE t.event.id = :eventId AND t.row = :row AND t.place = :place")
    int updateTicketStatus(@Param("eventId") Integer eventId, @Param("row") Integer row, @Param("place") Integer place, @Param("status") String status);
}
