package com.pointtils.pointtils.src.core.domain.entities.enums;

public enum Gender { MALE, FEMALE, OTHERS;
    public static Gender fromString(String value) {
        if (value == null) return OTHERS;
        return switch (value.toUpperCase()) {
            case "M" -> MALE;
            case "F" -> FEMALE;
            default -> OTHERS;
        };
    }
}
