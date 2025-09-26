package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentModality { 
    ONLINE, PERSONALLY;

    // Mantém compatibilidade com o comportamento antigo: null => ONLINE
    @JsonCreator
    public static AppointmentModality fromJson(String value) {
        if (value == null) return ONLINE;
        String v = value.trim().toLowerCase();
        return switch (v) {
            case "presencial", "personally", "p" -> PERSONALLY;
            case "online", "remoto", "r" -> ONLINE;
            default -> throw new IllegalArgumentException("Valor inválido para AppointmentModality: '" + value + "'. Aceitos: online, remoto, presencial, personally");
        };
    }

    // Para manter testes antigos funcionais e evitar quebras abruptas
    @Deprecated
    public static AppointmentModality fromString(String value) {
        return fromJson(value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}