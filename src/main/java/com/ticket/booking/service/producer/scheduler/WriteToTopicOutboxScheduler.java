package com.ticket.booking.service.producer.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.booking.service.producer.entity.OutboxEvent;
import com.ticket.booking.service.producer.entity.TicketBookedEvent;
import com.ticket.booking.service.producer.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WriteToTopicOutboxScheduler {

    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String, TicketBookedEvent> kafka;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 5000) // every 5 sec
    public void processOutbox() {
        List<OutboxEvent> pending = outboxRepo.findByStatus("PENDING");

        for (OutboxEvent event : pending) {
            try {
                TicketBookedEvent tevent = mapper.readValue(event.getPayload(), TicketBookedEvent.class);
                kafka.send(event.getTopic(), event.getKeyvalue(), tevent);

                event.setStatus("SENT");
                event.setLastAttemptAt(LocalDateTime.now());
                outboxRepo.save(event);

                System.out.println("✅ Outbox sent: " + event.getId());
            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastAttemptAt(LocalDateTime.now());

                // event.setStatus("ERROR");
                if (event.getRetryCount() > 3) {
                    event.setStatus("FAILED"); // give up after 3 tries
                }

                outboxRepo.save(event);
                System.err.println("❌ Outbox failed for event " + event.getId() + ": " + ex.getMessage());
            }
        }
    }
}

