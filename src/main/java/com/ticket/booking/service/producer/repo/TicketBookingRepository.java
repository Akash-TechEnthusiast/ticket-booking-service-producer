package com.ticket.booking.service.producer.repo;

import com.ticket.booking.service.producer.entity.TicketBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketBookingRepository extends JpaRepository<TicketBooking, Long> {
}