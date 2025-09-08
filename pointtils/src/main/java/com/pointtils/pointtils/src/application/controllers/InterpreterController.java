package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterRegisterService;

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
}
