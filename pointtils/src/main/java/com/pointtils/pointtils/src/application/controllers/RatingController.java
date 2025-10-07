package com.pointtils.pointtils.src.application.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.services.RatingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating Controller", description = "Endpoints para gerenciar avaliações dos appointments")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<RatingResponseDTO>> postRating(@RequestBody RatingRequestDTO request, 
            @PathVariable UUID appointmentId) {
        RatingResponseDTO response = ratingService.createRating(request, appointmentId);
        ApiResponse<RatingResponseDTO> apiResponse = ApiResponse.success("Avaliação adicionada com sucesso", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RatingResponseDTO>>> getAllRatingsByInterpreterId(
            @RequestParam UUID interpreterId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByInterpreterId(interpreterId);
        ApiResponse<List<RatingResponseDTO>> apiResponse = ApiResponse.success("Avaliações obtidas com sucesso",
                ratings);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RatingResponseDTO>> patchRating(@RequestBody RatingPatchRequestDTO request,
            @PathVariable UUID ratingId) {
        RatingResponseDTO response = ratingService.patchRating(request, ratingId);
        ApiResponse<RatingResponseDTO> apiResponse = ApiResponse.success("Avaliação atualizada com sucesso", response);
        return ResponseEntity.ok(apiResponse);
    }

}
