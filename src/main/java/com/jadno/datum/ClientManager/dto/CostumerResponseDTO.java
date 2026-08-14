package com.jadno.datum.ClientManager.dto;

public class CostumerResponseDTO {

    private Long id;
    private String name;
    private String cpf;
    private String email;
    private Status status;

    public CostumerResponseDTO(String name, String cpf, String email, Status status) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.status = status;
    }
}
