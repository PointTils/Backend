package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.LocationResponseDTO;
import com.pointtils.pointtils.src.application.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/location")
@Tag(name = "Location Controller", description = "Endpoints para gerenciar dados de localização")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/states")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todas as UFs brasileiras")
    public LocationResponseDTO getStates() {
        return locationService.getAllStates();
    }

    @GetMapping("/states/{stateId}/cities")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os municípios de uma determinada UF brasileira")
    public LocationResponseDTO getCitiesByState(@PathVariable String stateId) {
        return locationService.getCitiesByState(stateId);
    }
}
