package com.pointtils.pointtils.src.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSpecialtyResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID specialtyId;
    private String specialtyName;
}
