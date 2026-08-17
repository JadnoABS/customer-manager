package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.client.ScoreApiClient;
import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    @Autowired
    private ScoreApiClient scoreApiClient;

    @Autowired
    private CustomerRepository customerRepository;

    private final Logger log = LoggerFactory.getLogger(ScoreService.class);

    public CustomerScoreDTO getCustomerScore(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + id + " not found!"));

        long startTime = System.currentTimeMillis();

        CustomerScoreDTO customerScore = scoreApiClient.getResourceByCpf(customer.getCpf());

        long endTime = System.currentTimeMillis();
        log.info("Score fetch time: {}ms", endTime - startTime);

        return customerScore;
    }
}
