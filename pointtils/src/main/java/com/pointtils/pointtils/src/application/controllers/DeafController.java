package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.DeafPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;
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
@RequestMapping("/v1/deaf-users")
@AllArgsConstructor
public class DeafController {
    private final DeafRegisterService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> createUser(@Valid @RequestBody DeafRequestDTO dto) {
        DeafResponseDTO created = service.registerPerson(dto);
        ApiResponse<DeafResponseDTO> response = ApiResponse.success("Usuário surdo cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> findById(@PathVariable UUID id) {
        DeafResponseDTO deaf = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo encontrado com sucesso", deaf));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<DeafResponseDTO>>> findAll() {
        List<DeafResponseDTO> deafUsers = service.findAll();
        ApiResponse<List<DeafResponseDTO>> response =
                ApiResponse.success("Usuários surdos encontrados com sucesso", deafUsers);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> updateUser(@PathVariable UUID id,
                                                                   @RequestBody @Valid DeafPatchRequestDTO dto) {
        DeafResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo atualizado com sucesso", updated));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> updateComplete(@PathVariable UUID id,
                                                                       @RequestBody @Valid DeafRequestDTO dto) {
        DeafResponseDTO updated = service.updateComplete(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo atualizado com sucesso", updated));
    }
}
