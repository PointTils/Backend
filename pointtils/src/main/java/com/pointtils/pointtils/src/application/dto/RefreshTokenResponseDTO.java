package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDTO {
    boolean success;
    String message;
    Data data;

    public record Data(TokensDTO tokens) {

    }
}
