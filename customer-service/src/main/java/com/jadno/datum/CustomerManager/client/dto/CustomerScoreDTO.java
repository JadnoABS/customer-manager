package com.jadno.datum.CustomerManager.client.dto;

public record CustomerScoreDTO(
        String cpf,
        Long score,
        String classification
) {
}
