package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.dto.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.DeafResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;
import com.pointtils.pointtils.src.core.domain.entities.Person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/deaf-users")
@RequiredArgsConstructor

public class DeafController{
    private final DeafRegisterService service;

    @PostMapping
    @Operation(summary = "Create a Deaf User", description = "Creates a new deaf user")
    public ResponseEntity<DeafResponseDTO> createUser(@RequestBody DeafRequestDTO dto) {
        DeafResponseDTO created = service.registerPerson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by its ID")
    public ResponseEntity<DeafResponseDTO> findById(@PathVariable Long id) {
        try {
            DeafResponseDTO surdo = service.findById(id);
            return ResponseEntity.ok(surdo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete User", description = "Deletes a User record by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update partially a Deaf User", description = "Updates specific fields of a deaf user by ID")
    public ResponseEntity<DeafResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid DeafRequestDTO dto) {
    try {
        DeafResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(updated);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}


}
