package com.jadno.datum.CustomerManager.dto;

public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String cpf;
    private String email;
    private Status status;

    public CustomerResponseDTO(Long id, String name, String cpf, String email, Status status) {
        this.id = id;
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
}
