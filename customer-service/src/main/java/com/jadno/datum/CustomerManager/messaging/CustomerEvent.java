package com.jadno.datum.CustomerManager.messaging;

import java.time.Instant;

public record CustomerEvent(
        String cpf,
        Instant occurredAt
) {
    public static CustomerEvent of(String cpf) {
        return new CustomerEvent(cpf, Instant.now());
    }
}