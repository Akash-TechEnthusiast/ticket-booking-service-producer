package com.ticket.booking.service.producer;

import com.ticket.booking.service.producer.entity.OutboxEvent;
import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReadFromTopic {
    // private final ProcessedTicketRepository repo;
    private final OutboxRepository repo;
    private final KafkaTemplate<String, TicketBookedEvent> kafka;

    @KafkaListener(topics = "ticket.confirmed", groupId = "ticket-confirmed")
    public void consume(TicketBookedEvent event) {
        System.out.println("Consumed booking: at producer side " + event);
        Optional<OutboxEvent> outboxEvent = repo.findByKeyvalue(String.valueOf(event.getBookingId()));
        if (outboxEvent.isPresent()) {
            OutboxEvent booking = outboxEvent.get();  // unwrap Optional
            booking.setStatus(event.getStatus());             // update status
            repo.save(booking);                         // save back to DB
        }


    }
}