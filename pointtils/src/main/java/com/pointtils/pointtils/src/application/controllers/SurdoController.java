package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.SurdoRequestDTO;
import com.pointtils.pointtils.src.application.dto.SurdoResponseDTO;
import com.pointtils.pointtils.src.application.services.SurdoRegisterService;
import com.pointtils.pointtils.src.core.domain.entities.Person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/deaf-users")
@RequiredArgsConstructor

public class SurdoController {
    private final SurdoRegisterService service;

    // public SurdoController(SurdoRegisterService service) {
    //     this.service = service;
    // }

    @PostMapping
    @Operation(summary = "Create a Deaf User", description = "Creates a new deaf user")
    public ResponseEntity<SurdoResponseDTO> createUser(@RequestBody SurdoRequestDTO dto) {
        SurdoResponseDTO created = service.registerPerson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
