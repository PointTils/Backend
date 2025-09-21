package com.pointtils.pointtils.src.application.controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<List<InterpreterResponseDTO>>> findAll() {
        List<InterpreterResponseDTO> interpreters = service.findAll();
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


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta um usuário intérprete por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
