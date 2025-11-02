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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.UpdateSpecialtyRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.SpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/specialties")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Specialty Controller", description = "Endpoints para gerenciamento de especialidades")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    @Operation(
            summary = "Obtém todas as especialidades",
            description = "Retorna lista de todas as especialidades cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidades encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Specialty.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<List<Specialty>>> getAllSpecialties() {
        List<Specialty> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(ApiResponseDTO.success("Especialidades encontradas com sucesso", specialties));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busca uma especialidades por ID",
            description = "Retorna os dados de uma especialidade específica pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidade encontrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Specialty.class))
            ),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Specialty>> getSpecialtyById(@PathVariable UUID id) {
        Specialty specialty = specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Especialidade encontrada com sucesso", specialty));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Busca especialidades por nome",
            description = "Permite buscar especialidades filtrando pelo nome"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidades encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Specialty.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetro de busca inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<List<Specialty>>> searchSpecialtiesByName(
            @Parameter(description = "Nome da especialidade para busca", required = true) @RequestParam String name) {
        List<Specialty> specialties = specialtyService.searchSpecialtiesByName(name);
        return ResponseEntity.ok(ApiResponseDTO.success("Especialidades encontradas com sucesso", specialties));
    }

    @PostMapping
    @Operation(
            summary = "Cria uma especialidade",
            description = "Cria uma nova especialidade no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especialidade criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Specialty.class))
            ),
            @ApiResponse(responseCode = "400", description = "Nome da especialidade inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "409", description = "Especialidade já existente"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Specialty>> createSpecialty(
            @Parameter(description = "Nome da especialidade", required = true) @RequestParam String name) {
        Specialty specialty = specialtyService.createSpecialty(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Especialidade criada com sucesso", specialty));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualiza uma especialidade por ID",
            description = "Atualiza completamente uma especialidade existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Specialty.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Specialty>> updateSpecialty(@PathVariable UUID id, 
            @Parameter(description = "Novo nome da especialidade", required = true) @RequestParam String name) {
        Specialty specialty = specialtyService.updateSpecialty(id, name);
        return ResponseEntity.ok(ApiResponseDTO.success("Especialidade atualizada com sucesso", specialty));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualiza parcialmente uma especialidade por ID",
            description = "Atualiza campos específicos de uma especialidade"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Specialty.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados da atualização inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Specialty>> partialUpdateSpecialty(
            @PathVariable UUID id,
            @RequestBody UpdateSpecialtyRequestDTO request) {
        Specialty specialty = specialtyService.partialUpdateSpecialty(id, request.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("Especialidade atualizada com sucesso", specialty));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deleta uma especialidade por ID",
            description = "Remove permanentemente uma especialidade do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Especialidade deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Especialidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Especialidade está sendo utilizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> deleteSpecialty(@PathVariable UUID id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}
