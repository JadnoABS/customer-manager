package com.jadno.datum.CustomerManager.client;

import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.exception.ExternalServiceException;
import com.jadno.datum.CustomerManager.monitoring.MetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class ScoreApiClientTest {

    @Mock
    private MetricService metricService;

    @InjectMocks
    private ScoreApiClient scoreApiClient;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() throws Exception {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        ReflectionTestUtils.setField(scoreApiClient, "scoreApiRestClient", builder.baseUrl("http://score-api").build());
        when(metricService.setTimerScoreFetch(any())).thenAnswer(invocation -> {
            Callable<CustomerScoreDTO> request = invocation.getArgument(0);
            return request.call();
        });
    }

    @Test
    @DisplayName("Should deserialize a successful score response")
    void shouldReturnScore() {
        server.expect(requestTo("http://score-api/score/63276284006"))
                .andRespond(withSuccess(
                        "{\"cpf\":\"63276284006\",\"score\":750,\"classification\":\"LOW_RISK\"}",
                        MediaType.APPLICATION_JSON
                ));

        CustomerScoreDTO result = scoreApiClient.getResourceByCpf("63276284006");

        assertEquals("63276284006", result.cpf());
        assertEquals(750L, result.score());
        assertEquals("LOW_RISK", result.classification());
        verify(metricService).incrementScoreFetchSuccess();
        server.verify();
    }

    @Test
    @DisplayName("Should translate an unavailable score service into an external service error")
    void shouldTranslateHttpError() {
        server.expect(requestTo("http://score-api/score/63276284006"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> scoreApiClient.getResourceByCpf("63276284006")
        );

        assertEquals("Failed on requesting Score API (status 503 SERVICE_UNAVAILABLE)", exception.getMessage());
        verify(metricService).incrementScoreFetchError();
        server.verify();
    }

    @Test
    @DisplayName("Should translate a timeout into an external service error")
    void shouldTranslateTimeout() {
        server.expect(requestTo("http://score-api/score/63276284006"))
                .andRespond(withException(new SocketTimeoutException("Read timed out")));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> scoreApiClient.getResourceByCpf("63276284006")
        );

        assertEquals("Communication error with Score API", exception.getMessage());
        verify(metricService).incrementScoreFetchError();
        server.verify();
    }

    @Test
    @DisplayName("Should translate an unexpected response body into an external service error")
    void shouldTranslateMalformedResponse() {
        server.expect(requestTo("http://score-api/score/63276284006"))
                .andRespond(withSuccess("{not-json", MediaType.APPLICATION_JSON));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> scoreApiClient.getResourceByCpf("63276284006")
        );

        assertEquals("Communication error with Score API", exception.getMessage());
        verify(metricService).incrementScoreFetchError();
        server.verify();
    }
}
