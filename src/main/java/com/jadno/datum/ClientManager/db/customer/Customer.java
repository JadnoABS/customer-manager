package com.jadno.datum.ClientManager.db.customer;

import com.jadno.datum.ClientManager.dto.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    protected Customer() {
    }

    public Customer(String name, String cpf, String email, Status status) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Status getStatus() {
        return status;
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
}
