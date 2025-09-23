package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.responses.StateResponseDTO;
import com.pointtils.pointtils.src.application.services.StateService;
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
@RequestMapping("/v1/states")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "State Controller", description = "Endpoints para busca de dados de UFs brasileiras")
public class StateController {

    private final StateService stateService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as UFs brasileiras")
    public StateResponseDTO getStates() {
        return stateService.getAllStates();
    }

    @GetMapping("/{stateId}/cities")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todos os municípios de uma determinada UF brasileira")
    public StateResponseDTO getCitiesByState(@PathVariable String stateId) {
        return stateService.getCitiesByState(stateId);
    }
}
