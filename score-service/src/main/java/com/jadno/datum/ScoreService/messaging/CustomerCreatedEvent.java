package com.jadno.datum.ScoreService.messaging;

import java.time.Instant;

public record CustomerCreatedEvent(
        String cpf,
        Instant occurredAt
) {
}