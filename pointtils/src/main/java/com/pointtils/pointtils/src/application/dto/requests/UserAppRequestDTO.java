package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAppRequestDTO {

    @JsonProperty("device_id")
    @NotBlank(message = "Identificador do dispositivo do usuário deve ser informado")
    private String deviceId;

    @NotBlank(message = "Token do aplicativo do usuário deve ser informado")
    private String token;

    @NotBlank(message = "Plataforma do dispositivo do usuário deve ser informada")
    private String platform;

    @NotNull(message = "Identificador do usuário deve ser informado")
    private UUID userId;
}
