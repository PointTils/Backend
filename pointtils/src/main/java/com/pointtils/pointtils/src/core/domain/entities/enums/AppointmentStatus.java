package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    PENDING, ACCEPTED, CANCELED, COMPLETED;

    @JsonCreator
    public static AppointmentStatus fromJson(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status da solicitação não pode ser nulo");
        }
        String v = value.trim().toUpperCase();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Status da solicitação não pode ser vazio");
        }
        return switch (v) {
            case "PENDING" -> PENDING;
            case "ACCEPTED" -> ACCEPTED;
            case "CANCELED" -> CANCELED;
            case "COMPLETED" -> COMPLETED;
            default -> throw new IllegalArgumentException("Valor inválido para status da solicitação: '" + value + "'. Aceitos: PENDING, ACCEPTED, CANCELED, COMPLETED");
        };
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

