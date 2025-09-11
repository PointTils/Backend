package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterRegisterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/interpreters")
@AllArgsConstructor
public class InterpreterController {

    private final InterpreterRegisterService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> createInterpreter(
            @Valid @RequestBody InterpreterRequestDTO dto) {
        var interpreter = service.register(dto);
        ApiResponse<InterpreterResponseDTO> response =
                ApiResponse.success("Intérprete cadastrado com sucesso", interpreter);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> findById(@PathVariable UUID id) {
        InterpreterResponseDTO interpreter = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Intérprete encontrado com sucesso", interpreter));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<InterpreterResponseDTO>>> findAll() {
        List<InterpreterResponseDTO> interpreters = service.findAll();
        return ResponseEntity.ok(ApiResponse.success("Intérpretes encontrados com sucesso", interpreters));
    }


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> updateUser(@PathVariable UUID id,
                                                                          @RequestBody @Valid InterpreterPatchRequestDTO dto) {
        InterpreterResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Intérprete atualizado com sucesso", updated));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> updateComplete(@PathVariable UUID id,
                                                                              @RequestBody @Valid InterpreterRequestDTO dto) {
        InterpreterResponseDTO updated = service.updateComplete(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Intérprete atualizado com sucesso", updated));
    }
}
