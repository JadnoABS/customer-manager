package com.jadno.datum.ScoreService.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class CustomerScoreController {

    @GetMapping("/{cpf}")
    public ResponseEntity<ScoreDTO> fetchCustomerScore(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok(
                new ScoreDTO("12345678901", 750l, ScoreDTO.Classification.LOW_RISK)
        );
    }
}
