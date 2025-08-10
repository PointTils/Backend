package com.pointtils.pointtils.src.application.controllers.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRequestDTO {
    private String userId;
    private String description;
    private String type; // ENTRY, EXIT, LUNCH_START, LUNCH_END
}
