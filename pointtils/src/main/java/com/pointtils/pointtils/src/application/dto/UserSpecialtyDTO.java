package com.pointtils.pointtils.src.application.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSpecialtyDTO {
    private UUID id;
    @JsonProperty("user_id")
    private UUID userId;
    @JsonProperty("specialty_id")
    private UUID specialtyId;
    @JsonProperty("specialty_name")
    private String specialtyName;
}
