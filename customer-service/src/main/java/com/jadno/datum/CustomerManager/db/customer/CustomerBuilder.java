package com.jadno.datum.CustomerManager.db.customer;

import com.jadno.datum.CustomerManager.dto.Status;

public class CustomerBuilder {

    private Long id;
    private String name;
    private String cpf;
    private String email;
    private Status status = Status.ACTIVE;

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public CustomerBuilder email(String email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public Customer build() {
        if (id != null) return new Customer(id, name, cpf, email, status);
        return new Customer(name, cpf, email, status);
    }

}
