package com.jadno.datum.CustomerManager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${score-api.base-url}")
    private String scoreApiBaseUrl;

    @Bean
    @Qualifier("scoreApi")
    public RestClient scoreApiRestClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(scoreApiBaseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}