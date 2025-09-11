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

import com.pointtils.pointtils.src.application.dto.UpdateSpecialtyRequestDTO;
import com.pointtils.pointtils.src.application.services.SpecialtyService;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/specialties")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Specialty Controller", description = "Endpoints para gerenciar especialidades")
public class SpecialtyController {
    
    private final SpecialtyService specialtyService;
    
    @GetMapping
    public ResponseEntity<List<Specialty>> getAllSpecialties() {
        try {
            List<Specialty> specialties = specialtyService.getAllSpecialties();
            return ResponseEntity.ok(specialties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Specialty> getSpecialtyById(@PathVariable UUID id) {
        try {
            Specialty specialty = specialtyService.getSpecialtyById(id);
            return ResponseEntity.ok(specialty);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Specialty>> searchSpecialtiesByName(@RequestParam String name) {
        try {
            List<Specialty> specialties = specialtyService.searchSpecialtiesByName(name);
            return ResponseEntity.ok(specialties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Specialty> createSpecialty(@RequestParam String name) {
        try {
            Specialty specialty = specialtyService.createSpecialty(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(specialty);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Specialty> updateSpecialty(@PathVariable UUID id, @RequestParam String name) {
        try {
            Specialty specialty = specialtyService.updateSpecialty(id, name);
            return ResponseEntity.ok(specialty);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Specialty> partialUpdateSpecialty(
            @PathVariable UUID id, 
            @RequestBody UpdateSpecialtyRequestDTO request) {
        try {
            Specialty specialty = specialtyService.partialUpdateSpecialty(id, request.getName());
            return ResponseEntity.ok(specialty);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable UUID id) {
        try {
            specialtyService.deleteSpecialty(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
