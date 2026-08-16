package com.jadno.datum.ScoreService.dto;

public enum Classification {
    LOW_RISK,
    MEDIUM_RISK,
    HIGH_RISK;

    public static Classification forScore(Long score) {
        if(score >= 700) return LOW_RISK;
        if(score < 500) return HIGH_RISK;
        return MEDIUM_RISK;
    }
}