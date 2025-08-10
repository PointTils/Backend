package com.pointtils.pointtils.src.application.controllers.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponseDTO {
    private Long id;
    private String userId;
    private String description;
    private LocalDateTime timestamp;
    private String type;
}
