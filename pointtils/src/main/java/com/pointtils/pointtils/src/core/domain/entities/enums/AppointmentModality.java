package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentModality { 
    ONLINE, PERSONALLY;

    @JsonCreator
    public static AppointmentModality fromJson(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Modalidade da solicitação não pode ser nulo");
        }
        String v = value.trim().toUpperCase();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Modalidade da solicitação não pode ser vazio");
        }
        return switch (v) {
            case "PERSONALLY" -> PERSONALLY;
            case "ONLINE" -> ONLINE;
            default -> throw new IllegalArgumentException("Valor inválido para modalidade da solicitação: '" + value + "'. Aceitos: ONLINE, PERSONALLY");
        };
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}