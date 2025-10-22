package com.pointtils.pointtils.src.application.dto.responses;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private UUID id;
    private BigDecimal stars;
    private String description;
    private String date;
    private RatingUserResponseDTO user;
}
