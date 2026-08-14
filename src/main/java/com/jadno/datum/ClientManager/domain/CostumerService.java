package com.jadno.datum.ClientManager.domain;

import com.jadno.datum.ClientManager.dto.CostumerRequestDTO;
import com.jadno.datum.ClientManager.dto.CostumerResponseDTO;
import com.jadno.datum.ClientManager.db.costumer.Costumer;
import com.jadno.datum.ClientManager.db.costumer.CostumerRepository;
import com.jadno.datum.ClientManager.exception.CostumerNotFoundException;
import com.jadno.datum.ClientManager.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CostumerService {

    @Autowired
    private CostumerRepository costumerRepository;

    public CostumerResponseDTO create(CostumerRequestDTO costumer) {
        Costumer newCostumer;
        try {
            newCostumer = costumerRepository.save(Costumer.builder()
                    .name(costumer.getName())
                    .cpf(costumer.getCpf())
                    .email(costumer.getEmail())
                    .status(costumer.getStatus())
                    .build());
        } catch (Exception e) {
            throw new DAOException("Error on Costumer entity creation!");
        }

        // TODO: Enviar para a fila do rabbitmq

        return toCostumerDTO(newCostumer);
    }

    public CostumerResponseDTO update(CostumerRequestDTO costumer, Long costumerId) {
        int rowsAffected = costumerRepository.updateById(costumerId, costumer.getName(),
                costumer.getCpf(), costumer.getEmail(), costumer.getStatus().name());

        if(rowsAffected == 0) {
            throw new CostumerNotFoundException("Costumer with id: " + costumerId + " not found! Context: Costumer update");
        }

        Costumer updatedCostumer = costumerRepository.findById(costumerId)
                .orElseThrow(() -> new CostumerNotFoundException("Costumer with id: " + costumerId + " not found! Context: Costumer update"));

        return toCostumerDTO(updatedCostumer);
    }

    public void delete(Long costumerId) {
        costumerRepository.deleteById(costumerId);
    }

    public CostumerResponseDTO getWithId(Long costumerId) {
        return toCostumerDTO(costumerRepository.findById(costumerId)
                .orElseThrow(() -> new CostumerNotFoundException("Costumer with id: " + costumerId + " not found! Context: Costumer read")));
    }

    public List<CostumerResponseDTO> getAll() {
        return costumerRepository.findAll().stream()
                .map((costumer) -> toCostumerDTO(costumer)).toList();
    }

    public List<CostumerResponseDTO> getAllWithName(String name) {
        return costumerRepository.findByName(name).orElse(new ArrayList<>()).stream()
                .map((costumer -> toCostumerDTO(costumer))).toList();
    }

    public List<CostumerResponseDTO> getAllWithStatus(String status) {
        return costumerRepository.findByStatus(status).orElse(new ArrayList<>()).stream()
                .map((costumer -> toCostumerDTO(costumer))).toList();
    }

    private CostumerResponseDTO toCostumerDTO(Costumer costumer) {
        return new CostumerResponseDTO(
                costumer.getName(),
                costumer.getCpf(),
                costumer.getEmail(),
                costumer.getStatus());
    }
}
