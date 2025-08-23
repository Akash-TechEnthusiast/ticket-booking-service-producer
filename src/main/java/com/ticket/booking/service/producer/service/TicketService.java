package com.ticket.booking.service.producer.service;


import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.repo.TicketBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketBookingRepository repo;
    private final KafkaTemplate<String, Object> kafka;


    @Transactional
    public TicketBooking bookTicket(TicketBooking booking) {
        booking.setStatus("PENDING");
        TicketBooking saved = repo.save(booking);


        TicketBookedEvent event = new TicketBookedEvent(
                saved.getId(), saved.getPassengerName(), saved.getTrainId(), "BOOKED");


        kafka.send("ticket.booked", saved.getId().toString(), event);
        saved.setStatus("SENT");
        return repo.save(saved);
    }
}