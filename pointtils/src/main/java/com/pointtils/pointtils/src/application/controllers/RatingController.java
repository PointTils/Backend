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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.services.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating Controller", description = "Endpoints para gerenciar avaliações dos appointments")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{appointmentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Adiciona uma avaliação a um agendamento",
            description = "Cria uma nova avaliação para um agendamento específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados da avaliação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BadRequest.class))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BadRequest.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternalError.class)))
    })
    public ResponseEntity<ApiResponseDTO<RatingResponseDTO>> postRating(@Valid @RequestBody RatingRequestDTO request,
                                                                        @PathVariable UUID appointmentId) {
        RatingResponseDTO response = ratingService.createRating(request, appointmentId);
        ApiResponseDTO<RatingResponseDTO> apiResponse = ApiResponseDTO.success("Avaliação adicionada com sucesso", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtém todas as avaliações de um intérprete")
    public ResponseEntity<ApiResponseDTO<List<RatingResponseDTO>>> getAllRatingsByInterpreterId(
            @RequestParam UUID interpreterId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByInterpreterId(interpreterId);
        ApiResponseDTO<List<RatingResponseDTO>> apiResponse = ApiResponseDTO.success("Avaliações obtidas com sucesso",
                ratings);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza uma avaliação existente")
    public ResponseEntity<ApiResponseDTO<RatingResponseDTO>> patchRating(@RequestBody RatingPatchRequestDTO request,
                                                                         @PathVariable UUID id) {
        RatingResponseDTO response = ratingService.patchRating(request, id);
        ApiResponseDTO<RatingResponseDTO> apiResponse = ApiResponseDTO.success("Avaliação atualizada com sucesso", response);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta uma avaliação existente")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRating(@PathVariable UUID id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }

}
