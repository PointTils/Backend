package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.SurdoResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/deaf-users")
@AllArgsConstructor
public class DeafController {
    private final DeafRegisterService service;

    @PostMapping
    @Operation(summary = "Create a Deaf User", description = "Creates a new deaf user")
    public ResponseEntity<SurdoResponseDTO> createUser(@RequestBody DeafRequestDTO dto) {
        DeafResponseDTO created = service.registerPerson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get point by ID", description = "Retrieves a specific point by its ID")
    public ResponseEntity<DeafResponseDTO> findById(@PathVariable Long id) {
        try {
            DeafResponseDTO deaf = service.findById(id);
            return ResponseEntity.ok(deaf);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
