package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

import com.pointtils.pointtils.src.application.dto.AddUserSpecialtiesRequestDTO;
import com.pointtils.pointtils.src.application.dto.UserSpecialtiesResponseDTO;
import com.pointtils.pointtils.src.application.dto.UserSpecialtyResponseDTO;
import com.pointtils.pointtils.src.application.services.UserSpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users/{userId}/specialties")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Specialty Controller", description = "Endpoints para gerenciar especialidades de usuários")
public class UserSpecialtyController {
    
    private final UserSpecialtyService userSpecialtyService;
    
    @GetMapping
    public ResponseEntity<UserSpecialtiesResponseDTO> getUserSpecialties(@PathVariable UUID userId) {
        try {
            List<UserSpecialty> userSpecialties = userSpecialtyService.getUserSpecialties(userId);
            
            List<UserSpecialtyResponseDTO> responseDTOs = userSpecialties.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new UserSpecialtiesResponseDTO(false, e.getMessage(), null));
        }
    }
    
    @PostMapping
    public ResponseEntity<UserSpecialtiesResponseDTO> addUserSpecialties(
            @PathVariable UUID userId,
            @RequestBody AddUserSpecialtiesRequestDTO request) {
        try {
            List<UserSpecialty> addedSpecialties = userSpecialtyService.addUserSpecialties(
                userId, request.getSpecialtyIds(), request.isReplaceExisting());
            
            List<UserSpecialtyResponseDTO> responseDTOs = addedSpecialties.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserSpecialtiesResponseDTO(false, e.getMessage(), null));
        }
    }
    
    @PutMapping
    public ResponseEntity<UserSpecialtiesResponseDTO> replaceUserSpecialties(
            @PathVariable UUID userId,
            @RequestBody List<UUID> specialtyIds) {
        try {
            List<UserSpecialty> replacedSpecialties = userSpecialtyService.replaceUserSpecialties(userId, specialtyIds);
            
            List<UserSpecialtyResponseDTO> responseDTOs = replacedSpecialties.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserSpecialtiesResponseDTO(false, e.getMessage(), null));
        }
    }
    
    @PatchMapping("/{userSpecialtyId}")
    public ResponseEntity<UserSpecialtyResponseDTO> updateUserSpecialty(
            @PathVariable UUID userId,
            @PathVariable UUID userSpecialtyId,
            @RequestParam UUID newSpecialtyId) {
        try {
            UserSpecialty updatedUserSpecialty = userSpecialtyService.updateUserSpecialty(
                userSpecialtyId, userId, newSpecialtyId);
            
            UserSpecialtyResponseDTO responseDTO = convertToResponseDTO(updatedUserSpecialty);
            
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> removeUserSpecialty(
            @PathVariable UUID userId,
            @PathVariable UUID specialtyId) {
        try {
            userSpecialtyService.removeUserSpecialty(userId, specialtyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping
    public ResponseEntity<UserSpecialtiesResponseDTO> removeUserSpecialties(
            @PathVariable UUID userId,
            @RequestParam List<UUID> specialtyIds) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserSpecialtiesResponseDTO(false, e.getMessage(), null));
        }
    }
    
    private UserSpecialtyResponseDTO convertToResponseDTO(UserSpecialty userSpecialty) {
        return new UserSpecialtyResponseDTO(
            userSpecialty.getId(),
            userSpecialty.getUser().getId(),
            userSpecialty.getSpecialty().getId(),
            userSpecialty.getSpecialty().getName()
        );
    }
}
