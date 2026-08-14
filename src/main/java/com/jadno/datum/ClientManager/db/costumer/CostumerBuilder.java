package com.jadno.datum.ClientManager.db.costumer;

import com.jadno.datum.ClientManager.dto.Status;

public class CostumerBuilder {

    private String name;
    private String cpf;
    private String email;
    private Status status = Status.ACTIVE;

    public CostumerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CostumerBuilder cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public CostumerBuilder email(String email) {
        this.email = email;
        return this;
    }

    public CostumerBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public Costumer build() {
        return new Costumer(name, cpf, email, status);
    }

}
