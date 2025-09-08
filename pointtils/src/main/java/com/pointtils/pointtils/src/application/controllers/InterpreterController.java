package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
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
            ApiResponse<InterpreterResponseDTO> errorResponse = ApiResponse.error("Erro ao cadastrar intérprete: " + e.getMessage());
                ApiResponse.error("Erro ao cadastrar intérprete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InterpreterResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid InterpreterRequestDTO dto) {
        try {
            InterpreterResponseDTO updated = service.updatePartial(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
