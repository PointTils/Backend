package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<RatingResponseDTO>> postRating(@RequestBody RatingRequestDTO request) {
        RatingResponseDTO response = ratingService.createRating(request);
        ApiResponse<RatingResponseDTO> apiResponse = ApiResponse.success("Avaliação adicionada com sucesso", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
}
