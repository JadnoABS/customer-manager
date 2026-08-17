package com.jadno.datum.CustomerManager.api;

import com.jadno.datum.CustomerManager.dto.CustomerScoreDTO;
import com.jadno.datum.CustomerManager.domain.CustomerService;
import com.jadno.datum.CustomerManager.domain.ScoreService;
import com.jadno.datum.CustomerManager.dto.CustomerRequestDTO;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ScoreService scoreService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customer) {
        return ResponseEntity.ok(customerService.create(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerRequestDTO customer
    ) {
        return ResponseEntity.ok(customerService.update(id, customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCustomer(@PathVariable("id") Long id) {
        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getWithId(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) Status status
    ) {
        return ResponseEntity.ok(customerService.getAll(name, status));
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<CustomerScoreDTO> getCustomerScore(@PathVariable Long id) {
        return ResponseEntity.ok(scoreService.getCustomerScore(id));
    }
}
