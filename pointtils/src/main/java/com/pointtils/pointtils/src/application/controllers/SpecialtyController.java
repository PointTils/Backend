package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UpdateSpecialtyRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.services.SpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/specialties")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Specialty Controller", description = "Endpoints para gerenciamento de especialidades")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    @Operation(summary = "Obtém todas as especialidades")
    public ResponseEntity<ApiResponse<List<Specialty>>> getAllSpecialties() {
        List<Specialty> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(ApiResponse.success("Especialidades encontradas com sucesso", specialties));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma especialidades por ID")
    public ResponseEntity<ApiResponse<Specialty>> getSpecialtyById(@PathVariable UUID id) {
        Specialty specialty = specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(ApiResponse.success("Especialidade encontrada com sucesso", specialty));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca especialidades por nome")
    public ResponseEntity<ApiResponse<List<Specialty>>> searchSpecialtiesByName(@RequestParam String name) {
        List<Specialty> specialties = specialtyService.searchSpecialtiesByName(name);
        return ResponseEntity.ok(ApiResponse.success("Especialidades encontradas com sucesso", specialties));
    }

    @PostMapping
    @Operation(summary = "Cria uma especialidade")
    public ResponseEntity<ApiResponse<Specialty>> createSpecialty(@RequestParam String name) {
        Specialty specialty = specialtyService.createSpecialty(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Especialidade criada com sucesso", specialty));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma especialidade por ID")
    public ResponseEntity<ApiResponse<Specialty>> updateSpecialty(@PathVariable UUID id, @RequestParam String name) {
        Specialty specialty = specialtyService.updateSpecialty(id, name);
        return ResponseEntity.ok(ApiResponse.success("Especialidade atualizada com sucesso", specialty));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente uma especialidade por ID")
    public ResponseEntity<ApiResponse<Specialty>> partialUpdateSpecialty(
            @PathVariable UUID id,
            @RequestBody UpdateSpecialtyRequestDTO request) {
        Specialty specialty = specialtyService.partialUpdateSpecialty(id, request.getName());
        return ResponseEntity.ok(ApiResponse.success("Especialidade atualizada com sucesso", specialty));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma especialidade por ID")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable UUID id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}
