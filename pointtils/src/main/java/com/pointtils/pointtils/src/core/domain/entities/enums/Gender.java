package com.pointtils.pointtils.src.core.domain.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE, OTHERS;

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Gênero não preenchido");
        return switch (value.toUpperCase()) {
            case "MALE" -> MALE;
            case "FEMALE" -> FEMALE;
            case "OTHERS" -> OTHERS;
            default -> throw new IllegalArgumentException("Gênero inválido");
        };
    }
}
