package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;

import com.pointtils.pointtils.src.application.dto.requests.AddUserSpecialtiesRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserSpecialtiesResponseDTO;
import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
import com.pointtils.pointtils.src.application.services.UserSpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/v1/users/{userId}/specialties")
@RequiredArgsConstructor
@Tag(name = "User Specialty Controller", description = "Endpoints para gerenciamento de especialidades dos usuários")
public class UserSpecialtyController {

    private final UserSpecialtyService userSpecialtyService;

    @GetMapping
    @Operation(summary = "Busca todas as especialidades de um determinado usuário")
    public ResponseEntity<UserSpecialtiesResponseDTO> getUserSpecialties(@PathVariable UUID userId) {
        List<UserSpecialty> userSpecialties = userSpecialtyService.getUserSpecialties(userId);

        List<UserSpecialtyDTO> responseDTOs = userSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades do usuário obtidas com sucesso",
                new UserSpecialtiesResponseDTO.Data(
                        responseDTOs,
                        new UserSpecialtiesResponseDTO.Summary(
                                responseDTOs.size(),
                                responseDTOs.size(),
                                0
                        )
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Associa especialidades a um determinado usuário")
    public ResponseEntity<UserSpecialtiesResponseDTO> addUserSpecialties(
            @PathVariable UUID userId,
            @RequestBody AddUserSpecialtiesRequestDTO request) {
        List<UserSpecialty> addedSpecialties = userSpecialtyService.addUserSpecialties(
                userId, request.getSpecialtyIds(), request.isReplaceExisting());

        List<UserSpecialtyDTO> responseDTOs = addedSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        long totalUserSpecialties = userSpecialtyService.countUserSpecialties(userId);
        int duplicatesIgnored = request.getSpecialtyIds().size() - addedSpecialties.size();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades adicionadas com sucesso",
                new UserSpecialtiesResponseDTO.Data(
                        responseDTOs,
                        new UserSpecialtiesResponseDTO.Summary(
                                addedSpecialties.size(),
                                (int) totalUserSpecialties,
                                duplicatesIgnored
                        )
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @Operation(summary = "Substitui especialidades de um determinado usuário")
    public ResponseEntity<UserSpecialtiesResponseDTO> replaceUserSpecialties(
            @PathVariable UUID userId,
            @RequestBody List<UUID> specialtyIds) {
        List<UserSpecialty> replacedSpecialties = userSpecialtyService.replaceUserSpecialties(userId, specialtyIds);

        List<UserSpecialtyDTO> responseDTOs = replacedSpecialties.stream()
                .map(this::convertToResponseDTO)
                .toList();

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades do usuário atualizadas com sucesso",
                new UserSpecialtiesResponseDTO.Data(
                        responseDTOs,
                        new UserSpecialtiesResponseDTO.Summary(
                                replacedSpecialties.size(),
                                replacedSpecialties.size(),
                                0
                        )
                )
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userSpecialtyId}")
    @Operation(summary = "Atualiza uma especialidade de um determinado usuário")
    public ResponseEntity<UserSpecialtyDTO> updateUserSpecialty(
            @PathVariable UUID userId,
            @PathVariable UUID userSpecialtyId,
            @RequestParam UUID newSpecialtyId) {
        UserSpecialty updatedUserSpecialty = userSpecialtyService.updateUserSpecialty(
                userSpecialtyId, userId, newSpecialtyId);

        UserSpecialtyDTO responseDTO = convertToResponseDTO(updatedUserSpecialty);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{specialtyId}")
    @Operation(summary = "Remove uma especialidade de um determinado usuário")
    public ResponseEntity<Void> removeUserSpecialty(
            @PathVariable UUID userId,
            @PathVariable UUID specialtyId) {
        userSpecialtyService.removeUserSpecialty(userId, specialtyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Remove especialidades de um determinado usuário")
    public ResponseEntity<UserSpecialtiesResponseDTO> removeUserSpecialties(
            @PathVariable UUID userId,
            @RequestParam List<UUID> specialtyIds) {
        userSpecialtyService.removeUserSpecialties(userId, specialtyIds);

        UserSpecialtiesResponseDTO response = new UserSpecialtiesResponseDTO(
                true,
                "Especialidades removidas com sucesso",
                new UserSpecialtiesResponseDTO.Data(
                        null,
                        new UserSpecialtiesResponseDTO.Summary(
                                0,
                                (int) userSpecialtyService.countUserSpecialties(userId),
                                0
                        )
                )
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
