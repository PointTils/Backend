package com.pointtils.pointtils.src.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenRequestDTO {
    @JsonProperty("refresh_token")
    private String refreshToken;

    public RefreshTokenRequestDTO() {
    }

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
