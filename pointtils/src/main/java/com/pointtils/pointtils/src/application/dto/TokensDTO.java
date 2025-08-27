package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokensDTO {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private long expires_in;
    private long refresh_expires_in;
}
