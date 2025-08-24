package com.ticket.booking.service.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketBookingServiceProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBookingServiceProducerApplication.class, args);
    }

}
