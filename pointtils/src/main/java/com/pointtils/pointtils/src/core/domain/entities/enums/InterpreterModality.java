package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InterpreterModality {
    ONLINE, PERSONALLY, ALL;

    @JsonCreator
    public static InterpreterModality fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Modalidade não preenchida");
        return switch (value.toUpperCase()) {
            case "PERSONALLY" -> PERSONALLY;
            case "ONLINE" -> ONLINE;
            case "ALL" -> ALL;
            default -> throw new IllegalArgumentException("Modalidade inválida");
        };
    }
}
