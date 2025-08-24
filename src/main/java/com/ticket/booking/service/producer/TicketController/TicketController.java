package com.ticket.booking.service.producer.TicketController;


import com.ticket.booking.service.producer.entity.TicketBooking;
import com.ticket.booking.service.producer.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(
            summary = "Book a new ticket",
            description = "Creates a ticket booking with user, train, and payment details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket booked successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketBooking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TicketBooking> book(@RequestBody TicketBooking booking) throws IOException {
        return ResponseEntity.ok(service.bookTicket(booking));
    }
}