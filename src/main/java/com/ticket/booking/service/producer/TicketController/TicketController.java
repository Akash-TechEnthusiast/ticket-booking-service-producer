package com.ticket.booking.service.producer.TicketController;


import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService service;


    @PostMapping
    public ResponseEntity<TicketBooking> book(@RequestBody TicketBooking booking) throws IOException {
        return ResponseEntity.ok(service.bookTicket(booking));
    }
}