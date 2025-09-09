package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/deaf-users")
@AllArgsConstructor


public class DeafController {
    private final DeafRegisterService service;

    @PostMapping("register/person")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> createUser(@Valid @RequestBody DeafRequestDTO dto) {
        try {
            DeafResponseDTO created = service.registerPerson(dto);
        ApiResponse<DeafResponseDTO> response = ApiResponse.success("Usuário surdo cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<DeafResponseDTO> errorResponse =
                ApiResponse.error("Erro ao cadastrar usuário surdo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeafResponseDTO> findById(@PathVariable UUID id) {
        try {
            DeafResponseDTO deaf = service.findById(id);
            return ResponseEntity.ok(deaf);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeafResponseDTO>>> findAll() {
        try {
            List<DeafResponseDTO> deafUsers = service.findAll();
            ApiResponse<List<DeafResponseDTO>> response = 
                ApiResponse.success("Usuários surdos encontrados com sucesso", deafUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<DeafResponseDTO>> errorResponse = 
                ApiResponse.error("Erro ao buscar usuários surdos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeafResponseDTO> updateUser(@PathVariable UUID id, @RequestBody @Valid DeafRequestDTO dto) {
    try {
        DeafResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(updated);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> updateComplete(@PathVariable UUID id, @RequestBody @Valid DeafRequestDTO dto) {
        try {
            DeafResponseDTO updated = service.updateComplete(id, dto);
            ApiResponse<DeafResponseDTO> response = 
                ApiResponse.success("Usuário surdo atualizado com sucesso", updated);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            ApiResponse<DeafResponseDTO> errorResponse = 
                ApiResponse.error("Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<DeafResponseDTO> errorResponse = 
                ApiResponse.error("Erro ao atualizar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


}
