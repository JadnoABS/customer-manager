package com.jadno.datum.ScoreService.db;

import com.jadno.datum.ScoreService.dto.Classification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ScoreRepositoryTest {

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    @DisplayName("Should persist and find a score by CPF")
    void shouldFindScoreByCpf() {
        scoreRepository.saveAndFlush(new Score("63276284006", 850L, Classification.LOW_RISK));

        Score found = scoreRepository.findByCpf("63276284006").orElseThrow();

        assertEquals(850L, found.getScore());
        assertEquals(Classification.LOW_RISK, found.getClassification());
        assertTrue(scoreRepository.findByCpf("00000000000").isEmpty());
    }

    @Test
    @DisplayName("Should enforce one score per CPF")
    void shouldRejectDuplicatedCpf() {
        scoreRepository.saveAndFlush(new Score("63276284006", 450L, Classification.HIGH_RISK));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> scoreRepository.saveAndFlush(new Score("63276284006", 750L, Classification.LOW_RISK))
        );
    }
}
