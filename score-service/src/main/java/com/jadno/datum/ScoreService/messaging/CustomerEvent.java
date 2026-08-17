package com.jadno.datum.ScoreService.messaging;

import java.time.Instant;

public record CustomerEvent(
        String cpf,
        Instant occurredAt
) {
}