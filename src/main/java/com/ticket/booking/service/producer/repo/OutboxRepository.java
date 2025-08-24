package com.ticket.booking.service.producer.repo;

import com.ticket.booking.service.producer.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(String status);

    Optional<OutboxEvent> findByKeyvalue(String keyvalue);
}