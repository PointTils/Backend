package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    PENDING, ACCEPTED, CANCELED, COMPLETED;

    @JsonCreator
    public static AppointmentStatus fromJson(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor null não é aceito para AppointmentStatus");
        }
        String v = value.trim().toLowerCase();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Valor vazio não é aceito para AppointmentStatus");
        }
        return switch (v) {
            case "pending", "pendente" -> PENDING;
            case "accepted", "aceito" -> ACCEPTED;
            case "canceled", "cancelado" -> CANCELED;
            case "completed", "completado" -> COMPLETED;
            default -> throw new IllegalArgumentException("Valor inválido para AppointmentStatus: '" + value + "'. Aceitos: pending, accepted, canceled, completed");
        };
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

