package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.messaging.CustomerEventPublisher;
import com.jadno.datum.CustomerManager.exception.BusinessException;
import com.jadno.datum.CustomerManager.monitoring.MetricService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerEventPublisher eventPublisher;

    @Mock
    private MetricService metricService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Should create a customer successfully and publish an event")
    void shouldCreateCustomerSuccessfully() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("ClienteDatum", "80781456088", "cliente@datum.com", Status.ACTIVE);
        Customer savedCustomer = Customer.builder()
                .id(1L)
                .name("ClienteDatum")
                .cpf("80781456088")
                .email("cliente@datum.com")
                .status(Status.ACTIVE)
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.create(requestDTO);

        assertNotNull(result);
        assertEquals("ClienteDatum", result.getName());
        assertEquals("80781456088", result.getCpf());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(eventPublisher, times(1)).publishCustomerCreatedEvent(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to create a customer with an existing CPF")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("ClienteDatum2", "41679715003", "cliente2@datum.com", Status.ACTIVE);
        Customer savedCustomer = Customer.builder()
                .id(1L)
                .name("ClienteDatum")
                .cpf("41679715003")
                .email("cliente@datum.com")
                .status(Status.ACTIVE)
                .build();

        when(customerRepository.findByCpf("41679715003")).thenReturn(Optional.of(savedCustomer));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            customerService.create(requestDTO);
        });

        assertEquals("CPF number already in use!", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to create a customer with an existing e-mail")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("ClienteDatum2", "63276284006", "cliente@datum.com", Status.ACTIVE);
        Customer savedCustomer = Customer.builder()
                .id(1L)
                .name("ClienteDatum")
                .cpf("41679715003")
                .email("cliente@datum.com")
                .status(Status.ACTIVE)
                .build();

        when(customerRepository.findByEmail("cliente@datum.com")).thenReturn(Optional.of(savedCustomer));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            customerService.create(requestDTO);
        });

        assertEquals("E-mail address already in use!", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}