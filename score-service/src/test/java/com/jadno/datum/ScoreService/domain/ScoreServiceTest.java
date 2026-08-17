package com.jadno.datum.ScoreService.domain;

import com.jadno.datum.ScoreService.db.Score;
import com.jadno.datum.ScoreService.db.ScoreRepository;
import com.jadno.datum.ScoreService.dto.Classification;
import com.jadno.datum.ScoreService.dto.ScoreDTO;
import com.jadno.datum.ScoreService.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private ScoreRepository scoreRepository;

    @InjectMocks
    private ScoreService scoreService;

    @Captor
    private ArgumentCaptor<Score> scoreCaptor;

    private final String CPF = "12345678900";

    @Test
    void shouldReturnScoreWhenCpfExists() {
        Score mockScore = Score.builder()
                .cpf(CPF)
                .score(850L)
                .classification(Classification.forScore(850L))
                .build();

        when(scoreRepository.findByCpf(CPF)).thenReturn(Optional.of(mockScore));

        ScoreDTO result = scoreService.getCustomerScore(CPF);

        assertNotNull(result);
        assertEquals(CPF, result.cpf());
        assertEquals(850L, result.score());

        verify(scoreRepository, times(1)).findByCpf(CPF);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCpfDoesNotExist() {
        when(scoreRepository.findByCpf(CPF)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scoreService.getCustomerScore(CPF);
        });

        assertEquals("Customer with CPF " + CPF + " not found!", exception.getMessage());
        verify(scoreRepository, times(1)).findByCpf(CPF);
    }

    @Test
    void shouldSaveScoreSuccessfully() {
        scoreService.createScore(CPF);

        verify(scoreRepository, times(1)).save(scoreCaptor.capture());
        Score savedScore = scoreCaptor.getValue();

        assertEquals(CPF, savedScore.getCpf());
        assertNotNull(savedScore.getScore());
        assertTrue(savedScore.getScore() >= 0 && savedScore.getScore() <= 1000);
        assertNotNull(savedScore.getClassification());
    }

    @Test
    void shouldNotThrowExceptionWhenSaveFails() {
        doThrow(new RuntimeException("Database connection error"))
                .when(scoreRepository).save(any(Score.class));

        assertDoesNotThrow(() -> {
            scoreService.createScore(CPF);
        });

        verify(scoreRepository, times(1)).save(any(Score.class));
    }
}