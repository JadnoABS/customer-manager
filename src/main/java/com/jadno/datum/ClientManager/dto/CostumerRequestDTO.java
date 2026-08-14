package com.jadno.datum.ClientManager.dto;

import com.jadno.datum.ClientManager.validation.CPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CostumerRequestDTO {

    @NotBlank(message = "name field is required!")
    @Size(max = 30, message = "name field is 30 characters maximum")
    private String name;

    @NotBlank(message = "CPF field is required!")
    @CPF
    private String cpf;

    @NotBlank(message = "email field is required!")
    @Email(message = "Invalid email field")
    private String email;

    private Status status;

    public CostumerRequestDTO() {
    }

    public CostumerRequestDTO(String name, String cpf, String email, Status status) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.status = status;
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