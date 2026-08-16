package com.jadno.datum.ScoreService.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Optional<Score> findByCpf(String cpf);
}
