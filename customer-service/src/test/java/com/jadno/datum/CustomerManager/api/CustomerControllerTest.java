package com.jadno.datum.CustomerManager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadno.datum.CustomerManager.domain.CustomerService;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.Status;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    @DisplayName("Should return 201 Created when a valid customer is posted")
    void shouldReturn201WhenCreatingCustomer() throws Exception {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("ClienteDatum", "98765432100", "cliente@datum.com", Status.ACTIVE);
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(1L, "ClienteDatum", "98765432100", "cliente@datum.com", Status.ACTIVE);

        when(customerService.create(any(CustomerRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ClienteDatum"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.email").value("cliente@datum.com"))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.name()));
    }

    @Test
    @DisplayName("Should return 200 OK and the customer data when searching by ID")
    void shouldReturn200WhenFetchingCustomerById() throws Exception {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(1L, "ClienteDatum", "98765432100", "cliente@datum.com", Status.ACTIVE);

        when(customerService.getWithId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/customer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ClienteDatum"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.email").value("cliente@datum.com"))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.name()));
    }
}