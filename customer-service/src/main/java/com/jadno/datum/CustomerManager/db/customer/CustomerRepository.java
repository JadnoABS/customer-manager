package com.jadno.datum.CustomerManager.db.customer;

import com.jadno.datum.CustomerManager.dto.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<List<Customer>> findByName(String name);

    Optional<List<Customer>> findByStatus(Status status);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE customer
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
    @Query(value = "DELETE FROM customer WHERE id = :id", nativeQuery = true)
    void deleteById(
            @Param("id") Long id
    );
}
