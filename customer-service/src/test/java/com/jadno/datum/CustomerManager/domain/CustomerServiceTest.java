package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerJdbcRepository;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.exception.BusinessException;
import com.jadno.datum.CustomerManager.exception.CustomerNotFoundException;
import com.jadno.datum.CustomerManager.exception.DAOException;
import com.jadno.datum.CustomerManager.messaging.CustomerEventPublisher;
import com.jadno.datum.CustomerManager.monitoring.MetricService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerJdbcRepository customerJdbcRepository;

    @Mock
    private CustomerEventPublisher eventPublisher;

    @Mock
    private MetricService metricService;

    @InjectMocks
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Test
    @DisplayName("Should create a customer, normalize the CPF and publish an event")
    void shouldCreateCustomerSuccessfully() {
        CustomerRequestDTO request = request("807.814.560-88", "cliente@datum.com");
        Customer savedCustomer = customer(1L, "80781456088", "cliente@datum.com", Status.ACTIVE);

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponseDTO result = customerService.create(request);

        verify(customerRepository).findByCpf("80781456088");
        verify(customerRepository).findByEmail("cliente@datum.com");
        verify(customerRepository).save(customerCaptor.capture());
        assertEquals("80781456088", customerCaptor.getValue().getCpf());
        verify(eventPublisher).publishCustomerCreatedEvent("80781456088");
        verify(metricService).incrementCustomerCreateSuccess();
        assertEquals("Cliente Datum", result.getName());
        assertEquals("80781456088", result.getCpf());
    }

    @Test
    @DisplayName("Should reject an existing CPF before checking the e-mail")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        CustomerRequestDTO request = request("416.797.150-03", "novo@datum.com");
        when(customerRepository.findByCpf("41679715003"))
                .thenReturn(Optional.of(customer(1L, "41679715003", "cliente@datum.com", Status.ACTIVE)));

        BusinessException exception = assertThrows(BusinessException.class, () -> customerService.create(request));

        assertEquals("CPF number already in use!", exception.getMessage());
        verify(customerRepository, never()).findByEmail(any());
        verify(customerRepository, never()).save(any());
        verify(eventPublisher, never()).publishCustomerCreatedEvent(any());
    }

    @Test
    @DisplayName("Should reject an existing e-mail")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CustomerRequestDTO request = request("63276284006", "cliente@datum.com");
        when(customerRepository.findByEmail("cliente@datum.com"))
                .thenReturn(Optional.of(customer(1L, "41679715003", "cliente@datum.com", Status.ACTIVE)));

        BusinessException exception = assertThrows(BusinessException.class, () -> customerService.create(request));

        assertEquals("E-mail address already in use!", exception.getMessage());
        verify(customerRepository, never()).save(any());
        verify(eventPublisher, never()).publishCustomerCreatedEvent(any());
    }

    @Test
    @DisplayName("Should translate persistence errors during creation")
    void shouldTranslateRepositoryFailureWhenCreatingCustomer() {
        CustomerRequestDTO request = request("63276284006", "cliente@datum.com");
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("database unavailable"));

        DAOException exception = assertThrows(DAOException.class, () -> customerService.create(request));

        assertEquals("Error on Customer creation! Please try again later.", exception.getMessage());
        verify(metricService).incrementCustomerCreateError();
        verify(metricService, never()).incrementCustomerCreateSuccess();
        verify(eventPublisher, never()).publishCustomerCreatedEvent(any());
    }

    @Test
    @DisplayName("Should update a customer through the native query and publish a new score event")
    void shouldUpdateCustomerSuccessfully() {
        CustomerRequestDTO request = request("63276284006", "atualizado@datum.com");
        Customer updated = customer(7L, "63276284006", "atualizado@datum.com", Status.ACTIVE);
        when(customerRepository.updateById(7L, "Cliente Datum", "63276284006", "atualizado@datum.com", "ACTIVE"))
                .thenReturn(1);
        when(customerRepository.findById(7L)).thenReturn(Optional.of(updated));

        CustomerResponseDTO result = customerService.update(7L, request);

        assertEquals(7L, result.getId());
        assertEquals("atualizado@datum.com", result.getEmail());
        verify(eventPublisher).publishCustomerCreatedEvent("63276284006");
    }

    @Test
    @DisplayName("Should report a missing customer when the native update changes no rows")
    void shouldThrowWhenUpdatingMissingCustomer() {
        CustomerRequestDTO request = request("63276284006", "cliente@datum.com");
        when(customerRepository.updateById(99L, "Cliente Datum", "63276284006", "cliente@datum.com", "ACTIVE"))
                .thenReturn(0);

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.update(99L, request)
        );

        assertEquals("Customer with id: 99 not found!", exception.getMessage());
        verify(customerRepository, never()).findById(99L);
        verify(eventPublisher, never()).publishCustomerCreatedEvent(any());
    }

    @Test
    @DisplayName("Should delegate customer deletion to the repository")
    void shouldDeleteCustomer() {
        customerService.delete(3L);

        verify(customerRepository).deleteById(3L);
    }

    @Test
    @DisplayName("Should return a customer by id")
    void shouldReturnCustomerById() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer(1L, "63276284006", "cliente@datum.com", Status.ACTIVE)));

        CustomerResponseDTO result = customerService.getWithId(1L);

        assertEquals(1L, result.getId());
        assertEquals(Status.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("Should report a missing customer when searching by id")
    void shouldThrowWhenCustomerIdDoesNotExist() {
        when(customerRepository.findById(42L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getWithId(42L)
        );

        assertEquals("Customer with id: 42 not found!", exception.getMessage());
    }

    @Test
    @DisplayName("Should delegate filters to JdbcTemplate and map all results")
    void shouldReturnCustomersFilteredByNameAndStatus() {
        when(customerJdbcRepository.search("datum", Status.ACTIVE)).thenReturn(List.of(
                customer(1L, "63276284006", "a@datum.com", Status.ACTIVE),
                customer(2L, "03695141069", "b@datum.com", Status.ACTIVE)
        ));

        List<CustomerResponseDTO> result = customerService.getAll("datum", Status.ACTIVE);

        assertEquals(2, result.size());
        assertEquals("63276284006", result.get(0).getCpf());
        assertEquals("03695141069", result.get(1).getCpf());
    }

    private CustomerRequestDTO request(String cpf, String email) {
        return new CustomerRequestDTO("Cliente Datum", cpf, email, Status.ACTIVE);
    }

    private Customer customer(Long id, String cpf, String email, Status status) {
        return Customer.builder()
                .id(id)
                .name("Cliente Datum")
                .cpf(cpf)
                .email(email)
                .status(status)
                .build();
    }
}
