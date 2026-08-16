package com.jadno.datum.ScoreService.api;

public record ScoreDTO(String cpf, Long score, Classification classification) {

    public enum Classification { LOW_RISK, MEDIUM_RISK, HIGH_RISK }
}
