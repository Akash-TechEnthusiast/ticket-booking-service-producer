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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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

        try {
            // Wait max 5 seconds for Kafka ack
            SendResult<String, TicketBookedEvent> result =
                    future.get(5, TimeUnit.SECONDS);

            // ✅ Success case
            System.out.println("✅ Sent message -> " +
                    "topic=" + result.getRecordMetadata().topic() +
                    ", partition=" + result.getRecordMetadata().partition() +
                    ", offset=" + result.getRecordMetadata().offset());

            saved.setStatus("SENT");
        } catch (TimeoutException e) {
            System.err.println("⏰ Timeout: Kafka did not respond in time");
            saved.setStatus("TIMEOUT");
        } catch (org.apache.kafka.common.errors.SerializationException e) {
            System.err.println("⚠️ Serialization error: " + e.getMessage());
            saved.setStatus("SERIALIZATION_ERROR");
        } catch (org.apache.kafka.common.errors.UnknownTopicOrPartitionException e) {
            System.err.println("⚠️ Topic/partition error: " + e.getMessage());
            saved.setStatus("TOPIC_ERROR");
        } catch (org.apache.kafka.common.errors.NotLeaderForPartitionException e) {
            System.err.println("⚠️ Leader not available: " + e.getMessage());
            saved.setStatus("LEADER_ERROR");
        } catch (Exception e) {
            System.err.println("❌ General failure: " + e.getMessage());
            saved.setStatus("FAILED");
        }

        return repo.save(saved);


    }
}
