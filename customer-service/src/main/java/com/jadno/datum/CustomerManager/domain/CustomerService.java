package com.jadno.datum.CustomerManager.domain;

import com.jadno.datum.CustomerManager.db.customer.CustomerJdbcRepository;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.db.customer.Customer;
import com.jadno.datum.CustomerManager.db.customer.CustomerRepository;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.exception.CustomerNotFoundException;
import com.jadno.datum.CustomerManager.exception.DAOException;
import com.jadno.datum.CustomerManager.messaging.CustomerEventPublisher;
import com.jadno.datum.CustomerManager.monitoring.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    private CustomerEventPublisher customerEventPublisher;

    @Autowired
    private MetricService metricService;

    public CustomerResponseDTO create(CustomerRequestDTO customer) {
        Customer newCustomer;
        try {
            newCustomer = customerRepository.save(Customer.builder()
                    .name(customer.getName())
                    .cpf(customer.getCpf())
                    .email(customer.getEmail())
                    .status(customer.getStatus())
                    .build());
        } catch (Exception e) {
            metricService.incrementCustomerCreateError();
            throw new DAOException("Error on Customer creation! Email or Cpf may be already in use.");
        }

        customerEventPublisher.publishCustomerCreated(newCustomer.getCpf());
        metricService.incrementCustomerCreateSuccess();

        return toCustomerDTO(newCustomer);
    }

    public CustomerResponseDTO update(Long customerId, CustomerRequestDTO customer) {
        int rowsAffected = customerRepository.updateById(customerId, customer.getName(),
                customer.getCpf(), customer.getEmail(), customer.getStatus().name());

        if (rowsAffected == 0) {
            throw new CustomerNotFoundException("Customer with id: " + customerId + " not found!");
        }

        Customer updatedCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found!"));

        customerEventPublisher.publishCustomerCreated(updatedCustomer.getCpf());

        return toCustomerDTO(updatedCustomer);
    }

    public void delete(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public CustomerResponseDTO getWithId(Long customerId) {
        return toCustomerDTO(customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found!")));
    }

    public List<CustomerResponseDTO> getAll(String name, Status status) {
        return customerJdbcRepository.search(name, status).stream()
                .map(this::toCustomerDTO)
                .toList();
    }

    private CustomerResponseDTO toCustomerDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getStatus());
    }
}
