package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ParametersPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.application.services.ParametersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/parameters")
@AllArgsConstructor
@Tag(name = "Parameters Controller", description = "Endpoints para gerenciamento de parâmetros da aplicação")
public class ParametersController {

    private final ParametersService service;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastra um novo parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro de parâmetro realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<ParametersResponseDTO>> create(@Valid @RequestBody ParametersBasicRequestDTO dto) {
        ParametersResponseDTO created = service.create(dto);
        ApiResponseDTO<ParametersResponseDTO> response = ApiResponseDTO.success("Parâmetro cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os parâmetros da aplicação")
    public ResponseEntity<ApiResponseDTO<List<ParametersResponseDTO>>> findAll() {
        List<ParametersResponseDTO> list = service.findAll();
        ApiResponseDTO<List<ParametersResponseDTO>> response = new ApiResponseDTO<>(true, "Parâmetros encontrados com sucesso", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{key}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um parâmetro por chave")
    public ResponseEntity<ApiResponseDTO<ParametersResponseDTO>> findByKey(@PathVariable String key) {
        ParametersResponseDTO parameters = service.findByKey(key);
        ApiResponseDTO<ParametersResponseDTO> response = new ApiResponseDTO<>(true, "Parâmetro encontrado com sucesso", parameters);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um parâmetro por ID")
    public ResponseEntity<ApiResponseDTO<ParametersResponseDTO>> put(@PathVariable UUID id, @Valid @RequestBody ParametersPatchRequestDTO dto) {
        ParametersResponseDTO patched = service.put(id, dto);
        ApiResponseDTO<ParametersResponseDTO> response = new ApiResponseDTO<>(true, "Parâmetro atualizado com sucesso", patched);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta um parâmetro por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}