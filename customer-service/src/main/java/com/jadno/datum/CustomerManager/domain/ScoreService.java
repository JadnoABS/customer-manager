package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.client.ScoreApiClient;
import com.jadno.datum.CustomerManager.client.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    @Autowired
    private ScoreApiClient scoreApiClient;

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerScoreDTO getCustomerScore(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + id + " not found!"));

        return scoreApiClient.getResourceByCpf(customer.getCpf());
    }
}
