package com.jadno.datum.ScoreService.db;

import com.jadno.datum.ScoreService.ScoreServiceApplication;
import com.jadno.datum.ScoreService.dto.Classification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ScoreServiceApplication.class })
@Transactional
public class ScoreRepositoryIT {

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    public void shouldReturnOptionalWithScoreWhenCpfExists() {
        String cpf = "12345678900";
        Score score = new Score(cpf, 850L, Classification.LOW_RISK);
        scoreRepository.save(score);

        Optional<Score> foundScore = scoreRepository.findByCpf(cpf);

        assertTrue(foundScore.isPresent());
        assertEquals(cpf, foundScore.get().getCpf());
        assertEquals(Long.valueOf(850L), foundScore.get().getScore());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCpfDoesNotExist() {
        String cpf = "00000000000";

        Optional<Score> foundScore = scoreRepository.findByCpf(cpf);

        assertFalse(foundScore.isPresent());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldThrowExceptionWhenCpfIsDuplicated() {
        String cpf = "11111111111";
        Score score1 = new Score(cpf, 500L, Classification.MEDIUM_RISK);
        Score score2 = new Score(cpf, 700L, Classification.LOW_RISK);

        scoreRepository.save(score1);
        scoreRepository.flush();

        scoreRepository.save(score2);
        scoreRepository.flush();
    }
}