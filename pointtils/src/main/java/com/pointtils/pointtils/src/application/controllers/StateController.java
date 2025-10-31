package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.responses.StateResponseDTO;
import com.pointtils.pointtils.src.application.services.StateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/states")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "State Controller", description = "Endpoints para busca de dados de UFs brasileiras")
public class StateController {

    private final StateService stateService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Busca todas as UFs brasileiras",
            description = "Retorna lista de todos os estados (UFs) do Brasil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estados encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StateResponseDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public StateResponseDTO getStates() {
        return stateService.getAllStates();
    }

    @GetMapping("/{stateId}/cities")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Busca todos os municípios de uma determinada UF brasileira",
            description = "Retorna lista de todas as cidades de um estado específico brasileiro"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cidades encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StateResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "ID do estado inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Estado não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public StateResponseDTO getCitiesByState(
            @Parameter(description = "ID do estado (UF)", required = true) @PathVariable String stateId) {
        return stateService.getCitiesByState(stateId);
    }
}
