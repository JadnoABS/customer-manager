package com.jadno.datum.ScoreService.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificationTest {

    @ParameterizedTest(name = "score {0} should be classified as {1}")
    @CsvSource({
            "0, HIGH_RISK",
            "499, HIGH_RISK",
            "500, MEDIUM_RISK",
            "699, MEDIUM_RISK",
            "700, LOW_RISK",
            "1000, LOW_RISK"
    })
    void shouldClassifyScoreBoundaries(long score, Classification expected) {
        assertEquals(expected, Classification.forScore(score));
    }
}
