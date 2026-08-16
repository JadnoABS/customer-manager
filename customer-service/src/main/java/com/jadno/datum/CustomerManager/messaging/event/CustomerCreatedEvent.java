package com.jadno.datum.CustomerManager.messaging.event;

import java.time.Instant;

public record CustomerCreatedEvent(
    String cpf,
    Instant occurredAt
) {
    public static CustomerCreatedEvent of(String cpf) {
        return new CustomerCreatedEvent(cpf, Instant.now());
    }
}
