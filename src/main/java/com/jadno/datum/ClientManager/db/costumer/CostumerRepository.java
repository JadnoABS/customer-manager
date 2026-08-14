package com.jadno.datum.ClientManager.db.costumer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostumerRepository extends JpaRepository<Costumer, Long> {

    Optional<List<Costumer>> findByName(String name);

    Optional<List<Costumer>> findByStatus(String status);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE client
            SET name = :name,
                cpf = :cpf,
                email = :email,
                status = :status
            WHERE id = :id
            """, nativeQuery = true)
    int updateById(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("cpf") String cpf,
            @Param("email") String email,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM client WHERE id = :id", nativeQuery = true)
    void deleteById(
            @Param("id") Long id
    );
}
