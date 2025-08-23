package com.ticket.booking.service.producer;

import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.repo.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TicketBookedListener {
    // private final ProcessedTicketRepository repo;
    private final TicketBookingRepository repo;
    private final KafkaTemplate<String, TicketBookedEvent> kafka;

    @KafkaListener(topics = "ticket.confirmed", groupId = "ticket-confirmed")
    public void consume(TicketBookedEvent event) {
        System.out.println("Consumed booking: at producer side " + event);
        Optional<TicketBooking> ticketbook = repo.findById(event.getBookingId());
        if (ticketbook.isPresent()) {
            TicketBooking booking = ticketbook.get();   // unwrap Optional
            booking.setStatus("CONFIRMED");             // update status
            repo.save(booking);                         // save back to DB
        }


    }
}