package com.jadno.datum.ScoreService.domain;

import com.jadno.datum.ScoreService.db.Score;
import com.jadno.datum.ScoreService.db.ScoreRepository;
import com.jadno.datum.ScoreService.dto.Classification;
import com.jadno.datum.ScoreService.dto.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public ScoreDTO getCustomerScore(String cpf) {
        return toScoreDTO(
            scoreRepository.findByCpf(cpf).orElseThrow(RuntimeException::new)
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
            throw new RuntimeException(e);
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
