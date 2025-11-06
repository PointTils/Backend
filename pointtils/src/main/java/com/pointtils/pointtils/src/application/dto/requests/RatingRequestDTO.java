package com.pointtils.pointtils.src.application.dto.requests;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("appointment_id")
    private UUID appointmentId;
}
