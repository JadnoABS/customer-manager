package com.jadno.datum.ClientManager.domain;

import com.jadno.datum.ClientManager.dto.CustomerRequestDTO;
import com.jadno.datum.ClientManager.dto.CustomerResponseDTO;
import com.jadno.datum.ClientManager.db.customer.Customer;
import com.jadno.datum.ClientManager.db.customer.CustomerRepository;
import com.jadno.datum.ClientManager.dto.Status;
import com.jadno.datum.ClientManager.exception.CustomerNotFoundException;
import com.jadno.datum.ClientManager.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

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
            throw new DAOException("Error on Customer entity creation!");
        }

        // TODO: Enviar para a fila do rabbitmq

        return toCustomerDTO(newCustomer);
    }

    public CustomerResponseDTO update(Long customerId, CustomerRequestDTO customer) {
        int rowsAffected = customerRepository.updateById(customerId, customer.getName(),
                customer.getCpf(), customer.getEmail(), customer.getStatus().name());

        if(rowsAffected == 0) {
            throw new CustomerNotFoundException("Customer with id: " + customerId + " not found!");
        }

        Customer updatedCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found!"));

        return toCustomerDTO(updatedCustomer);
    }

    public void delete(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public CustomerResponseDTO getWithId(Long customerId) {
        return toCustomerDTO(customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found!")));
    }

    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream()
                .map((customer) -> toCustomerDTO(customer)).toList();
    }

    public List<CustomerResponseDTO> getAllWithName(String name) {
        return customerRepository.findByName(name).orElse(new ArrayList<>()).stream()
                .map((customer -> toCustomerDTO(customer))).toList();
    }

    public List<CustomerResponseDTO> getAllWithStatus(Status status) {
        return customerRepository.findByStatus(status).orElse(new ArrayList<>()).stream()
                .map((customer -> toCustomerDTO(customer))).toList();
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
