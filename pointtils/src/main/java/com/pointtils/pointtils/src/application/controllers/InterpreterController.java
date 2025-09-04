package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.ApiResponse;
import com.pointtils.pointtils.src.application.dto.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterRegisterService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/interpreters")
@AllArgsConstructor
public class InterpreterController {
    
    private final InterpreterRegisterService service;

    @PostMapping("/register/interpreter")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> createInterpreter(
            @Valid @RequestBody InterpreterRequestDTO dto) {
        
        var interpetrer = service.register(dto);

        ApiResponse<InterpreterResponseDTO> response = new ApiResponse<InterpreterResponseDTO>(true, "Interpreter registered with sucessely", interpetrer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
