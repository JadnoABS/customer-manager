package com.jadno.datum.CustomerManager.db.customer;

import com.jadno.datum.CustomerManager.dto.Status;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({CustomerJdbcRepository.class, CustomerRepositoryTest.CacheConfig.class})
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find customers by CPF and e-mail")
    void shouldFindCustomerByCpfAndEmail() {
        Customer saved = customerRepository.saveAndFlush(customer("Ana Datum", "63276284006", "ana@datum.com", Status.ACTIVE));

        assertEquals(saved.getId(), customerRepository.findByCpf("63276284006").orElseThrow().getId());
        assertEquals(saved.getId(), customerRepository.findByEmail("ana@datum.com").orElseThrow().getId());
        assertTrue(customerRepository.findByCpf("00000000000").isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique CPF and e-mail constraints")
    void shouldRejectDuplicatedCustomerData() {
        customerRepository.saveAndFlush(customer("Ana Datum", "63276284006", "ana@datum.com", Status.ACTIVE));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> customerRepository.saveAndFlush(customer("Outra Pessoa", "63276284006", "outra@datum.com", Status.ACTIVE))
        );
    }

    @Test
    @DisplayName("Should update a customer using the native query")
    void shouldUpdateCustomerUsingNativeQuery() {
        Customer saved = customerRepository.saveAndFlush(customer("Nome Antigo", "63276284006", "antigo@datum.com", Status.ACTIVE));

        int rows = customerRepository.updateById(
                saved.getId(),
                "Nome Atualizado",
                "03695141069",
                "novo@datum.com",
                "INACTIVE"
        );
        entityManager.clear();

        Customer updated = customerRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1, rows);
        assertEquals("Nome Atualizado", updated.getName());
        assertEquals("03695141069", updated.getCpf());
        assertEquals(Status.INACTIVE, updated.getStatus());
    }

    @Test
    @DisplayName("Should report zero affected rows when the native update id does not exist")
    void shouldNotUpdateMissingCustomer() {
        int rows = customerRepository.updateById(
                999L,
                "Cliente",
                "63276284006",
                "cliente@datum.com",
                "ACTIVE"
        );

        assertEquals(0, rows);
    }

    @Test
    @DisplayName("Should delete a customer using the native query")
    void shouldDeleteCustomerUsingNativeQuery() {
        Customer saved = customerRepository.saveAndFlush(customer("Ana Datum", "63276284006", "ana@datum.com", Status.ACTIVE));

        customerRepository.deleteById(saved.getId());
        entityManager.clear();

        assertFalse(customerRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Should filter customers by a case-insensitive partial name using JdbcTemplate")
    void shouldFilterByPartialNameUsingJdbcTemplate() {
        customerRepository.saveAllAndFlush(List.of(
                customer("Ana Datum", "63276284006", "ana@datum.com", Status.ACTIVE),
                customer("Bruno DATUM", "03695141069", "bruno@datum.com", Status.INACTIVE),
                customer("Carla Silva", "80781456088", "carla@datum.com", Status.ACTIVE)
        ));

        List<Customer> result = customerJdbcRepository.search("datum", null);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should combine name and status filters using JdbcTemplate")
    void shouldCombineNameAndStatusFiltersUsingJdbcTemplate() {
        customerRepository.saveAllAndFlush(List.of(
                customer("Ana Datum", "63276284006", "ana@datum.com", Status.ACTIVE),
                customer("Bruno Datum", "03695141069", "bruno@datum.com", Status.INACTIVE)
        ));

        List<Customer> result = customerJdbcRepository.search("datum", Status.INACTIVE);

        assertEquals(1, result.size());
        assertEquals("Bruno Datum", result.get(0).getName());
        assertEquals(Status.INACTIVE, result.get(0).getStatus());
    }

    private Customer customer(String name, String cpf, String email, Status status) {
        return Customer.builder()
                .name(name)
                .cpf(cpf)
                .email(email)
                .status(status)
                .build();
    }

    @TestConfiguration
    static class CacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }
}
