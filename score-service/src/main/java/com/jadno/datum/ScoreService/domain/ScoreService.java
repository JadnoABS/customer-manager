package com.jadno.datum.ScoreService.domain;

import com.jadno.datum.ScoreService.db.Score;
import com.jadno.datum.ScoreService.db.ScoreRepository;
import com.jadno.datum.ScoreService.dto.Classification;
import com.jadno.datum.ScoreService.dto.ScoreDTO;
import com.jadno.datum.ScoreService.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    private Logger log = LoggerFactory.getLogger(ScoreService.class);

    public ScoreDTO getCustomerScore(String cpf) {
        return toScoreDTO(
            scoreRepository.findByCpf(cpf).orElseThrow(() -> new ResourceNotFoundException("Customer with CPF " + cpf + " not found!"))
        );
    }

    public void createScore(String cpf) {
        Long score = (long) (Math.random() * 1001);
        Classification classification = Classification.forScore(score);

        try {
            scoreRepository.save(Score.builder()
                .cpf(cpf)
                .score(score)
                .classification(classification)
                .build()
            );
        } catch (Exception e) {
            log.warn("Error while saving customer score. Exception: {}", e.getMessage());
        }
    }

    private ScoreDTO toScoreDTO(Score score) {
        return new ScoreDTO(
                score.getCpf(),
                score.getScore(),
                score.getClassification()
        );
    }
}
