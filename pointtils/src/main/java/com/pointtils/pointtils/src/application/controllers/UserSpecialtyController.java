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

import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
import com.pointtils.pointtils.src.application.dto.requests.AddUserSpecialtiesRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserSpecialtiesResponseDTO;
import com.pointtils.pointtils.src.application.services.UserSpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;

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
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/v1/users/{userId}/specialties")
@RequiredArgsConstructor
@Tag(name = "User Specialty Controller", description = "Endpoints para gerenciamento de especialidades dos usuários")
public class UserSpecialtyController {

    private final UserSpecialtyService userSpecialtyService;

    @GetMapping
    @Operation(
            summary = "Busca todas as especialidades de um determinado usuário",
            description = "Retorna lista de especialidades associadas a um usuário específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidades do usuário encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserSpecialtyDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "ID do usuário inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<UserSpecialtiesResponseDTO> getUserSpecialties(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId) {
        List<UserSpecialty> userSpecialties = userSpecialtyService.getUserSpecialties(userId);

        List<UserSpecialtyDTO> responseDTOs = userSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades do usuário obtidas com sucesso",
                responseDTOs
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Associa especialidades a um determinado usuário",
            description = "Adiciona uma ou mais especialidades a um usuário específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especialidades adicionadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSpecialtiesResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou especialidade não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<UserSpecialtiesResponseDTO> addUserSpecialties(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId,
            @RequestBody AddUserSpecialtiesRequestDTO request) {
        List<UserSpecialty> addedSpecialties = userSpecialtyService.addUserSpecialties(
                userId, request.getSpecialtyIds(), request.isReplaceExisting());

        List<UserSpecialtyDTO> responseDTOs = addedSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades adicionadas com sucesso",
                responseDTOs
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @Operation(
            summary = "Substitui especialidades de um determinado usuário",
            description = "Substitui completamente as especialidades de um usuário pelas fornecidas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidades do usuário atualizadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserSpecialtyDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Lista de especialidade inválida"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou especialidades não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<UserSpecialtiesResponseDTO> replaceUserSpecialties(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId,
            @RequestBody List<UUID> specialtyIds) {
        List<UserSpecialty> replacedSpecialties = userSpecialtyService.replaceUserSpecialties(userId, specialtyIds);

        List<UserSpecialtyDTO> responseDTOs = replacedSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades do usuário atualizadas com sucesso",
                responseDTOs
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userSpecialtyId}")
    @Operation(
            summary = "Atualiza uma especialidade de um determinado usuário",
            description = "Atualiza uma associação específica usuário-especialidade"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidade do usuário atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSpecialtyDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "IDs inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário, especialidade ou associação"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<UserSpecialtyDTO> updateUserSpecialty(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId,
            @Parameter(description = "ID da associação usuário-especialidade", required = true) @PathVariable UUID userSpecialtyId,
            @Parameter(description = "ID da nova especialidade", required = true) @RequestParam UUID newSpecialtyId) {
        UserSpecialty updatedUserSpecialty = userSpecialtyService.updateUserSpecialty(
                userSpecialtyId, userId, newSpecialtyId);

        UserSpecialtyDTO responseDTO = convertToResponseDTO(updatedUserSpecialty);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{specialtyId}")
    @Operation(
            summary = "Remove uma especialidade de um determinado usuário",
            description = "Remove a associação entre um usuário e uma especialidade específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Especialidade removida com sucesso"),
            @ApiResponse(responseCode = "400", description = "IDs inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário, especialidade ou associação não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> removeUserSpecialty(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId,
            @Parameter(description = "ID da especialidade", required = true) @PathVariable UUID specialtyId) {
        userSpecialtyService.removeUserSpecialty(userId, specialtyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Remove especialidades de um determinado usuário",
            description = "Remove múltiplas associações entre um usuário e especialidades"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidades removidas com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserSpecialtyDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Lista de IDs inválida"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou especialidades não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<UserSpecialtiesResponseDTO> removeUserSpecialties(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID userId,
            @Parameter(description = "Lista de IDs das especialidades", required = true) @RequestParam List<UUID> specialtyIds) {
        userSpecialtyService.removeUserSpecialties(userId, specialtyIds);

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades removidas com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    private UserSpecialtyDTO convertToResponseDTO(UserSpecialty userSpecialty) {
        return new UserSpecialtyDTO(
                userSpecialty.getId(),
                userSpecialty.getUser().getId(),
                userSpecialty.getSpecialty().getId(),
                userSpecialty.getSpecialty().getName()
        );
    }
}
