package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.client.ScoreApiClient;
import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private ScoreApiClient scoreApiClient;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ScoreService scoreService;

    @Test
    @DisplayName("Should obtain the customer's CPF before requesting the external score")
    void shouldReturnCustomerScore() {
        Customer customer = Customer.builder()
                .id(5L)
                .name("Cliente Datum")
                .cpf("63276284006")
                .email("cliente@datum.com")
                .status(Status.ACTIVE)
                .build();
        CustomerScoreDTO expected = new CustomerScoreDTO("63276284006", 750L, "LOW_RISK");
        when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        when(scoreApiClient.getResourceByCpf("63276284006")).thenReturn(expected);

        CustomerScoreDTO result = scoreService.getCustomerScore(5L);

        assertSame(expected, result);
        verify(scoreApiClient).getResourceByCpf("63276284006");
    }

    @Test
    @DisplayName("Should not call the external service when the customer does not exist")
    void shouldThrowWhenCustomerDoesNotExist() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scoreService.getCustomerScore(99L)
        );

        assertEquals("Customer with id: 99 not found!", exception.getMessage());
        verify(scoreApiClient, never()).getResourceByCpf(org.mockito.ArgumentMatchers.anyString());
    }
}
