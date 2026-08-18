package com.jadno.datum.CustomerManager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadno.datum.CustomerManager.domain.CustomerService;
import com.jadno.datum.CustomerManager.domain.ScoreService;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.exception.BusinessException;
import com.jadno.datum.CustomerManager.exception.ExternalServiceException;
import com.jadno.datum.CustomerManager.exception.GlobalExceptionHandler;
import com.jadno.datum.CustomerManager.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CustomerService customerService;

    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 201 when a valid customer is created")
    void shouldReturn201WhenCreatingCustomer() throws Exception {
        CustomerRequestDTO request = validRequest();
        CustomerResponseDTO response = response(1L);
        when(customerService.create(any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cliente Datum"))
                .andExpect(jsonPath("$.cpf").value("63276284006"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should return 400 with validation details for required fields")
    void shouldReturn400ForInvalidCustomerPayload() throws Exception {
        CustomerRequestDTO invalid = new CustomerRequestDTO("", "123", "email-invalido", Status.ACTIVE);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error on sent fields"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("Should return 422 when a CPF is already registered")
    void shouldReturn422ForBusinessConflict() throws Exception {
        when(customerService.create(any(CustomerRequestDTO.class)))
                .thenThrow(new BusinessException("CPF number already in use!"));

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("CPF number already in use!"));
    }

    @Test
    @DisplayName("Should return 200 with the updated customer")
    void shouldReturn200WhenUpdatingCustomer() throws Exception {
        when(customerService.update(any(), any(CustomerRequestDTO.class))).thenReturn(response(1L));

        mockMvc.perform(put("/customer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Should return 404 when updating a missing customer")
    void shouldReturn404WhenUpdatingMissingCustomer() throws Exception {
        when(customerService.update(any(), any(CustomerRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Customer with id: 99 not found!"));

        mockMvc.perform(put("/customer/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/customer/99"));
    }

    @Test
    @DisplayName("Should return 200 when deleting a customer")
    void shouldReturn200WhenDeletingCustomer() throws Exception {
        mockMvc.perform(delete("/customer/{id}", 1L))
                .andExpect(status().isOk());

        verify(customerService).delete(1L);
    }

    @Test
    @DisplayName("Should return 200 and the customer when searching by id")
    void shouldReturn200WhenFetchingCustomerById() throws Exception {
        when(customerService.getWithId(1L)).thenReturn(response(1L));

        mockMvc.perform(get("/customer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("cliente@datum.com"));
    }

    @Test
    @DisplayName("Should return 404 when the customer id does not exist")
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        when(customerService.getWithId(42L))
                .thenThrow(new ResourceNotFoundException("Customer with id: 42 not found!"));

        mockMvc.perform(get("/customer/{id}", 42L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should forward name and status filters")
    void shouldReturnCustomersFilteredByNameAndStatus() throws Exception {
        when(customerService.getAll("datum", Status.ACTIVE)).thenReturn(List.of(response(1L)));

        mockMvc.perform(get("/customer")
                        .queryParam("name", "datum")
                        .queryParam("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(customerService).getAll("datum", Status.ACTIVE);
    }

    @Test
    @DisplayName("Should return the score obtained from the external service")
    void shouldReturnCustomerScore() throws Exception {
        when(scoreService.getCustomerScore(1L))
                .thenReturn(new CustomerScoreDTO("63276284006", 750L, "LOW_RISK"));

        mockMvc.perform(get("/customer/{id}/score", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("63276284006"))
                .andExpect(jsonPath("$.score").value(750))
                .andExpect(jsonPath("$.classification").value("LOW_RISK"));
    }

    @Test
    @DisplayName("Should return 502 when communication with the score service fails")
    void shouldReturn502WhenScoreServiceFails() throws Exception {
        when(scoreService.getCustomerScore(1L))
                .thenThrow(new ExternalServiceException("Communication error with Score API", new RuntimeException()));

        mockMvc.perform(get("/customer/{id}/score", 1L))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Communication error with Score API"));
    }

    private CustomerRequestDTO validRequest() {
        return new CustomerRequestDTO("Cliente Datum", "63276284006", "cliente@datum.com", Status.ACTIVE);
    }

    private CustomerResponseDTO response(Long id) {
        return new CustomerResponseDTO(id, "Cliente Datum", "63276284006", "cliente@datum.com", Status.ACTIVE);
    }
}
