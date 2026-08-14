package com.jadno.datum.ClientManager.api;

import com.jadno.datum.ClientManager.domain.CustomerService;
import com.jadno.datum.ClientManager.dto.CustomerRequestDTO;
import com.jadno.datum.ClientManager.dto.CustomerResponseDTO;
import com.jadno.datum.ClientManager.dto.Status;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    /* TODOs no projeto:
    - fazer testes unitarios
    - melhorar tratamento de erros (validação de campos por ex)
    - bloquear o Profile USER das rotas modificadoras
    - implementar rota de score
    - implementar o client
    - implementar rabbitmq
     */

    @Autowired
    private CustomerService customerService;

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
        if(name != null && !name.isEmpty()) {
            return ResponseEntity.ok(customerService.getAllWithName(name));
        }
        if(status != null) {
            return ResponseEntity.ok(customerService.getAllWithStatus(status));
        }
        return ResponseEntity.ok(customerService.getAll());
    }
}
