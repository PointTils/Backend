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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.application.services.EnterpriseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/enterprises")
@AllArgsConstructor
@Tag(name = "Enterprise Controller", description = "Endpoints para gerenciamento de usuários empresa")
public class EnterpriseController {
    private final EnterpriseService service;

    @PostMapping("/register")
    @Operation(
            summary = "Cadastra um usuário empresa",
            description = "Registra uma nova empresa no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnterpriseResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<EnterpriseResponseDTO>> createUser(@Valid @RequestBody EnterpriseRequestDTO dto) {
        EnterpriseResponseDTO created = service.registerEnterprise(dto);
        ApiResponseDTO<EnterpriseResponseDTO> response = new ApiResponseDTO<>(true, "Empresa cadastrada com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Busca todos os usuários empresa",
            description = "Retorna lista de todas as empresas cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresas encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EnterpriseResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<List<EnterpriseResponseDTO>>> findAll() {
        List<EnterpriseResponseDTO> enterpriseList = service.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success("Empresas encontradas com sucesso", enterpriseList));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Busca um usuário empresa por ID",
            description = "Retorna os dados de uma empresa específica pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnterpriseResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<EnterpriseResponseDTO>> findById(@PathVariable UUID id) {
        EnterpriseResponseDTO enterprise = service.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Empresa encontrada com sucesso", enterprise));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Atualiza um usuário empresa por ID",
            description = "Atualiza campos específicos de uma empresa"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnterpriseResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<EnterpriseResponseDTO>> updateUser(@PathVariable UUID id,
                                                                         @RequestBody @Valid EnterprisePatchRequestDTO dto) {
        EnterpriseResponseDTO updated = service.patchEnterprise(id, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Empresa atualizada com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Deleta um usuário empresa por ID",
            description = "Remove permanentemente uma empresa do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empresa deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
