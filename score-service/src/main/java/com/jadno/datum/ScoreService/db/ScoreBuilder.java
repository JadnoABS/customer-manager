package com.jadno.datum.ScoreService.db;

import com.jadno.datum.ScoreService.dto.Classification;

public class ScoreBuilder {

    private String cpf;

    private Long score;

    private Classification classification;

    public ScoreBuilder cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public ScoreBuilder score(Long score) {
        this.score = score;
        return this;
    }

    public ScoreBuilder classification(Classification classification) {
        this.classification = classification;
        return this;
    }

    public Score build() {
        return new Score(cpf, score, classification);
    }
}
