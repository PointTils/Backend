package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    boolean success;
    String message;
    Data data;

    public record Data(UserDTO user, TokensDTO tokens) {

    }
}
