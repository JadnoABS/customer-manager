package com.jadno.datum.ScoreService.api;

import com.jadno.datum.ScoreService.domain.ScoreService;
import com.jadno.datum.ScoreService.dto.Classification;
import com.jadno.datum.ScoreService.dto.ScoreDTO;
import com.jadno.datum.ScoreService.exception.GlobalExceptionHandler;
import com.jadno.datum.ScoreService.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomerScoreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private CustomerScoreController customerScoreController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(customerScoreController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturn200WhenCpfExists() throws Exception {
        String cpf = "12345678900";
        ScoreDTO mockScoreDto = new ScoreDTO(cpf, 700L, Classification.LOW_RISK);

        when(scoreService.getCustomerScore(cpf)).thenReturn(mockScoreDto);

        mockMvc.perform(get("/score/{cpf}", cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(scoreService).getCustomerScore(cpf);
    }

     @Test
     void shouldReturn404WhenCpfIsNotFound() throws Exception {
         String cpf = "11111111111";
         when(scoreService.getCustomerScore(cpf)).thenThrow(new ResourceNotFoundException("Customer with CPF " + cpf + " not found!"));

         mockMvc.perform(get("/score/{cpf}", cpf)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isNotFound());
     }

    @Test
    public void shouldReturns500WhenServiceThrowsException() throws Exception {
        String cpf = "00000000000";
        when(scoreService.getCustomerScore(cpf)).thenThrow(new RuntimeException(""));

        mockMvc.perform(get("/score/{cpf}", cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(scoreService).getCustomerScore(cpf);
    }
}