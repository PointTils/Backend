package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    PENDING, ACCEPTED, CANCELED, COMPLETED;

    // Mantém comportamento: null => PENDING. Valores inválidos => 400 (IllegalArgumentException)
    @JsonCreator
    public static AppointmentStatus fromJson(String value) {
        if (value == null) return PENDING;
        String v = value.trim().toLowerCase();
        return switch (v) {
            case "pending", "pendente" -> PENDING;
            case "accepted", "aceito" -> ACCEPTED;
            case "canceled", "cancelado" -> CANCELED;
            case "completed", "completado" -> COMPLETED;
            default -> throw new IllegalArgumentException("Valor inválido para AppointmentStatus: '" + value + "'. Aceitos: pending, accepted, canceled, completed");
        };
    }

    @Deprecated
    public static AppointmentStatus fromString(String value) {
        return fromJson(value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

