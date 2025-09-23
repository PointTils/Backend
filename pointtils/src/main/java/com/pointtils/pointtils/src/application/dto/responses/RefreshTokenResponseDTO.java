package com.pointtils.pointtils.src.application.dto.responses;

import com.pointtils.pointtils.src.application.dto.TokensDTO;
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
