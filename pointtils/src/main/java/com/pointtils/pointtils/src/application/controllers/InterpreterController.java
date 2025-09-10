package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterRegisterService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/interpreters")
@AllArgsConstructor
public class InterpreterController {
    
    private final InterpreterRegisterService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> createInterpreter(
            @Valid @RequestBody InterpreterRequestDTO dto) {
        try {
            var interpetrer = service.register(dto);
            ApiResponse<InterpreterResponseDTO> response =
                ApiResponse.success("Intérprete cadastrado com sucesso", interpetrer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<InterpreterResponseDTO> errorResponse = 
                ApiResponse.error("Erro ao cadastrar intérprete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<InterpreterResponseDTO> findById(@PathVariable UUID id) {
        try {
            InterpreterResponseDTO interpreter = service.findById(id);
            return ResponseEntity.ok(interpreter);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<InterpreterResponseDTO>>> findAll() {
        try {
            List<InterpreterResponseDTO> interpreters = service.findAll();
            ApiResponse<List<InterpreterResponseDTO>> response = 
                ApiResponse.success("Intérpretes encontrados com sucesso", interpreters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<InterpreterResponseDTO>> errorResponse = 
                ApiResponse.error("Erro ao buscar intérpretes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<InterpreterResponseDTO> updateUser(@PathVariable UUID id, @RequestBody @Valid InterpreterPatchRequestDTO dto) {
        try {
            InterpreterResponseDTO updated = service.updatePartial(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> updateComplete(@PathVariable UUID id, @RequestBody @Valid InterpreterRequestDTO dto) {
        try {
            InterpreterResponseDTO updated = service.updateComplete(id, dto);
            ApiResponse<InterpreterResponseDTO> response = 
                ApiResponse.success("Intérprete atualizado com sucesso", updated);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            ApiResponse<InterpreterResponseDTO> errorResponse = 
                ApiResponse.error("Intérprete não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<InterpreterResponseDTO> errorResponse = 
                ApiResponse.error("Erro ao atualizar intérprete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
