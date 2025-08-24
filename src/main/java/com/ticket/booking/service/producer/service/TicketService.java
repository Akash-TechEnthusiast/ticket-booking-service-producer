package com.ticket.booking.service.producer.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.booking.service.producer.entity.OutboxEvent;
import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.repo.OutboxRepository;
import com.ticket.booking.service.producer.repo.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketBookingRepository repo;
    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String, TicketBookedEvent> kafka;


    @Transactional
    public TicketBooking bookTicket(TicketBooking booking) throws IOException {
        booking.setStatus("PENDING");
        if (booking.getPassengerName() == null || booking.getTrainId() == null) {
            throw new IllegalArgumentException("PassengerName and TrainId are required");
        }
        TicketBooking saved = repo.save(booking);


        TicketBookedEvent event = new TicketBookedEvent(
                saved.getId(),
                saved.getPassengerName(),
                saved.getTrainId(),
                "PENDING"
        );

        // Save event to Outbox instead of sending directly
        OutboxEvent outbox = OutboxEvent.builder()
                .topic("ticket.booked")
                .keyvalue(saved.getId().toString())
                .payload(new ObjectMapper().writeValueAsString(event))
                .status("PENDING")
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        outboxRepo.save(outbox);

        return saved;


    }
}
