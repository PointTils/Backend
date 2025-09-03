package com.pointtils.pointtils.src.application.controllers;

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

import com.pointtils.pointtils.src.application.dto.ApiResponse;
import com.pointtils.pointtils.src.application.dto.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.DeafResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/deaf-users")
@AllArgsConstructor


public class DeafController {
    private final DeafRegisterService service;

    @PostMapping("register/person")
    public ResponseEntity<ApiResponse<DeafResponseDTO>> createUser(@Valid @RequestBody DeafRequestDTO dto) {
        DeafResponseDTO created = service.registerPerson(dto);
        ApiResponse<DeafResponseDTO> response = new ApiResponse<DeafResponseDTO>(true, "User Register Successfully!", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeafResponseDTO> findById(@PathVariable Long id) {
        try {
            DeafResponseDTO deaf = service.findById(id);
            return ResponseEntity.ok(deaf);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeafResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid DeafRequestDTO dto) {
    try {
        DeafResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(updated);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}


}
