package com.jadno.datum.CustomerManager.monitoring;

import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.exception.ExternalServiceException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class MetricService {

    @Autowired
    private MeterRegistry registry;

    public void incrementCustomerCreateSuccess() {
        registry.counter("customer.create.success").increment();
    }

    public void incrementCustomerCreateError() {
        registry.counter("customer.create.error").increment();
    }

    public void incrementScoreFetchSuccess() {
        registry.counter("score.fetch.success");
    }

    public void incrementScoreFetchError() {
        registry.counter("score.fetch.error");
    }

    public CustomerScoreDTO setTimerScoreFetch(Callable<CustomerScoreDTO> scoreFetch) throws Exception {
        return registry.timer("score.fetch.time").recordCallable(scoreFetch);
    }
}
