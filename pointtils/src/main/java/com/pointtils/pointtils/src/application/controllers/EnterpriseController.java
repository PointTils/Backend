package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.application.services.EnterpriseService;
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
@RequestMapping("/v1/enterprise-users")
@AllArgsConstructor
@Tag(name = "Enterprise Controller", description = "Endpoints para gerenciamento de usuários empresa")
public class EnterpriseController {
    private final EnterpriseService service;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um usuário empresa")
    public ResponseEntity<ApiResponse<EnterpriseResponseDTO>> createUser(@Valid @RequestBody EnterpriseRequestDTO dto) {
        EnterpriseResponseDTO created = service.registerEnterprise(dto);
        ApiResponse<EnterpriseResponseDTO> response = new ApiResponse<>(true, "Empresa cadastrada com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os usuários empresa")
    public ResponseEntity<ApiResponse<List<EnterpriseResponseDTO>>> findAll() {
        List<EnterpriseResponseDTO> enterpriseList = service.findAll();
        return ResponseEntity.ok(ApiResponse.success("Empresas encontradas com sucesso", enterpriseList));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um usuário empresa por ID")
    public ResponseEntity<ApiResponse<EnterpriseResponseDTO>> findById(@PathVariable UUID id) {
        EnterpriseResponseDTO enterprise = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Empresa encontrada com sucesso", enterprise));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um usuário empresa por ID")
    public ResponseEntity<EnterpriseResponseDTO> updateUser(@PathVariable UUID id,
                                                            @RequestBody @Valid EnterprisePatchRequestDTO dto) {
        EnterpriseResponseDTO updated = service.patchEnterprise(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta um usuário empresa por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
