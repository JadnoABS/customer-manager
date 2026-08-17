package com.jadno.datum.ScoreService.api;

import com.jadno.datum.ScoreService.domain.ScoreService;
import com.jadno.datum.ScoreService.dto.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class CustomerScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{cpf}")
    public ResponseEntity<ScoreDTO> fetchCustomerScore(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok(scoreService.getCustomerScore(cpf));
    }
}
