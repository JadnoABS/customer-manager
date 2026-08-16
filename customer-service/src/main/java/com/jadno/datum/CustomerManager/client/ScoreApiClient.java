package com.jadno.datum.CustomerManager.client;

import com.jadno.datum.CustomerManager.client.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ScoreApiClient {

    @Autowired
    @Qualifier("scoreApi")
    private RestClient scoreApiRestClient;

    private final Logger logger = LoggerFactory.getLogger(ScoreApiClient.class);

    public CustomerScoreDTO getResourceByCpf(String cpf) {
        try {
            return scoreApiRestClient.get()
                    .uri("/score/{cpf}", cpf)
                    .retrieve()
                    .body(CustomerScoreDTO.class);
        } catch (RestClientResponseException ex) {
            logger.warn("API externa retornou erro {} ao buscar recurso {}", ex.getStatusCode(), cpf);
            throw new ExternalServiceException(
                    "Falha ao consultar a API externa (status " + ex.getStatusCode() + ")", ex);
        } catch (Exception ex) {
            logger.error("Erro inesperado ao chamar API externa para o recurso {}", cpf, ex);
            throw new ExternalServiceException("Erro de comunicação com a API externa", ex);
        }
    }
}
