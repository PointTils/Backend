package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentModality { 
    ONLINE, PERSONALLY;

    @JsonCreator
    public static AppointmentModality fromJson(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor null não é aceito para AppointmentModality");
        }
        String v = value.trim().toLowerCase();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Valor vazio não é aceito para AppointmentModality");
        }
        return switch (v) {
            case "presencial", "personally", "p" -> PERSONALLY;
            case "online", "remoto", "r" -> ONLINE;
            default -> throw new IllegalArgumentException("Valor inválido para AppointmentModality: '" + value + "'. Aceitos: online, remoto, presencial, personally");
        };
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}