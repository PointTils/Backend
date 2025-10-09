package com.pointtils.pointtils.src.application.dto.responses;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingUserResponseDTO {
    private UUID id;
    private String name;
    private String picture;
}
