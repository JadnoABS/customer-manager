package com.jadno.datum.CustomerManager.client;

import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ScoreApiClient {

    @Autowired
    @Qualifier("scoreApi")
    private RestClient scoreApiRestClient;

    private final Logger log = LoggerFactory.getLogger(ScoreApiClient.class);

    @Cacheable(cacheNames = "customerScore", key = "#cpf")
    public CustomerScoreDTO getResourceByCpf(String cpf) {
        try {
            return scoreApiRestClient.get()
                    .uri("/score/{cpf}", cpf)
                    .retrieve()
                    .body(CustomerScoreDTO.class);
        } catch (RestClientResponseException ex) {
            log.warn("Score API returned error status {} while trying to fetch CPF {}", ex.getStatusCode(), cpf);
            throw new ExternalServiceException(
                    "Failed on requesting Score API (status " + ex.getStatusCode() + ")", ex);
        } catch (Exception ex) {
            log.error("Unexpected error while trying to request Score API for CPF {}", cpf, ex);
            throw new ExternalServiceException("Communication error with Score API", ex);
        }
    }
}
