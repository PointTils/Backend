package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.services.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating Controller", description = "Endpoints para gerenciar avaliações dos appointments")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{appointmentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adiciona uma avaliação a um agendamento")
    public ResponseEntity<ApiResponseDTO<RatingResponseDTO>> postRating(@RequestBody RatingRequestDTO request) {
        RatingResponseDTO response = ratingService.createRating(request);
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
