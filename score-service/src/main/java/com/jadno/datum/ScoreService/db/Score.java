package com.jadno.datum.ScoreService.db;

import com.jadno.datum.ScoreService.dto.Classification;
import jakarta.persistence.*;

@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private Long score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Classification classification;

    protected Score() { }

    public Score(String cpf, Long score, Classification classification) {
        this.cpf = cpf;
        this.score = score;
        this.classification = classification;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getScore() {
        return score;
    }

    public Classification getClassification() {
        return classification;
    }

    public static ScoreBuilder builder() {
        return new ScoreBuilder();
    }
}
