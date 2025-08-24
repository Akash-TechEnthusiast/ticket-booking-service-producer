package com.ticket.booking.service.producer.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;           // Kafka topic
    private String keyvalue;             // message key
    @Lob
    private String payload;         // JSON serialized event

    private String status;          // PENDING, SENT, FAILED
    private int retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime lastAttemptAt;
}
