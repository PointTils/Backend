package com.pointtils.pointtils.src.application.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.controllers.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.controllers.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.services.PointService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Tag(name = "Point", description = "Point management API")
public class PointController {

    private final PointService pointService;

    @GetMapping
    @Operation(summary = "List all points", description = "Retrieves a list of all registered points")
    public ResponseEntity<List<PointResponseDTO>> findAll() {
        List<PointResponseDTO> points = pointService.findAll();
        return ResponseEntity.ok(points);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get point by ID", description = "Retrieves a specific point by its ID")
    public ResponseEntity<PointResponseDTO> findById(@PathVariable Long id) {
        try {
            PointResponseDTO point = pointService.findById(id);
            return ResponseEntity.ok(point);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create point", description = "Creates a new point record")
    public ResponseEntity<PointResponseDTO> create(@RequestBody PointRequestDTO requestDTO) {
        PointResponseDTO createdPoint = pointService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPoint);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update point", description = "Updates an existing point record")
    public ResponseEntity<PointResponseDTO> update(@PathVariable Long id, @RequestBody PointRequestDTO requestDTO) {
        try {
            PointResponseDTO updatedPoint = pointService.update(id, requestDTO);
            return ResponseEntity.ok(updatedPoint);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete point", description = "Deletes a point record by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            pointService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
