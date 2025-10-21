package com.pointtils.pointtils.src.application.dto.requests;

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
public class RatingRequestDTO {
    
    private BigDecimal stars;
    private String description;
    private UUID appointmentId;
}
