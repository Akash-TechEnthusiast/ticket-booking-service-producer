package com.ticket.booking.service.producer.service;


import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.repo.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketBookingRepository repo;
    private final KafkaTemplate<String, TicketBookedEvent> kafka;


    @Transactional
    public TicketBooking bookTicket(TicketBooking booking) {
        booking.setStatus("PENDING");
        if (booking.getPassengerName() == null || booking.getTrainId() == null) {
            throw new IllegalArgumentException("PassengerName and TrainId are required");
        }
        TicketBooking saved = repo.save(booking);


        TicketBookedEvent event = new TicketBookedEvent(
                saved.getId(), saved.getPassengerName(), saved.getTrainId(), "BOOKED");


        //kafka.send("ticket.booked", saved.getId().toString(), event);


        CompletableFuture<SendResult<String, TicketBookedEvent>> future =
                kafka.send("ticket.booked", saved.getId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("✅ Sent message to topic: " +
                        result.getRecordMetadata().topic() +
                        " partition: " + result.getRecordMetadata().partition() +
                        " offset: " + result.getRecordMetadata().offset());
                saved.setStatus("SENT");
            } else {
                System.err.println("❌ Failed to send message: " + ex.getMessage());
                saved.setStatus("Failed to SENT");
            }
        });

        return repo.save(saved);


    }
}
