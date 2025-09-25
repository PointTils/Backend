package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;



@RestController
@RequestMapping("/v1/interpreters")
@AllArgsConstructor
@Tag(name = "Interpreter Controller", description = "Endpoints para gerenciamento de usuários intérprete")
public class InterpreterController {

    private final InterpreterService service;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um usuário intérprete")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> createInterpreter(
            @Valid @RequestBody InterpreterBasicRequestDTO dto) {
        var interpreter = service.registerBasic(dto);
        ApiResponse<InterpreterResponseDTO> response =
                ApiResponse.success("Intérprete cadastrado com sucesso", interpreter);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os usuários intérprete")
    public ResponseEntity<ApiResponse<List<InterpreterResponseDTO>>> findAll(
            @RequestParam(required = false) String modality,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false, name = "available_date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String availableDate) {
        List<InterpreterResponseDTO> interpreters = service.findAll(
                modality, gender, city, uf, neighborhood, specialty,
                availableDate);
        return ResponseEntity.ok(ApiResponse.success("Intérpretes encontrados com sucesso", interpreters));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um usuário intérprete por ID")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> findById(@PathVariable UUID id) {
        InterpreterResponseDTO interpreter = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Intérprete encontrado com sucesso", interpreter));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza parcialmente um usuário intérprete por ID")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> updateUser(@PathVariable UUID id,
            @RequestBody @Valid InterpreterPatchRequestDTO dto) {
        InterpreterResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Intérprete atualizado com sucesso", updated));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um usuário intérprete por ID")
    public ResponseEntity<ApiResponse<InterpreterResponseDTO>> updateComplete(@PathVariable UUID id,
            @RequestBody @Valid InterpreterBasicRequestDTO dto) {
        InterpreterResponseDTO updated = service.updateComplete(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Intérprete atualizado com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta um usuário intérprete por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
