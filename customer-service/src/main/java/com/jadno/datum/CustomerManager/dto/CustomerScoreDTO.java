package com.jadno.datum.CustomerManager.dto;

public record CustomerScoreDTO(
        String cpf,
        Long score,
        String classification
) {
}
