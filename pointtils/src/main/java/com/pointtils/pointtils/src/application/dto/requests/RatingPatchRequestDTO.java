package com.pointtils.pointtils.src.application.dto.requests;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingPatchRequestDTO {
    private BigDecimal stars;
    private String description;
}
