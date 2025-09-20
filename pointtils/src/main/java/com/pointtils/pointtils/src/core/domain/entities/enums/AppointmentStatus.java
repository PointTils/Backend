package com.pointtils.pointtils.src.core.domain.entities.enums;

public enum AppointmentStatus {
    PENDING, ACCEPTED, CANCELED, COMPLETED;
    
    public static AppointmentStatus fromString(String value) {
        if (value == null) return PENDING;
        return switch (value.toLowerCase()) {
            case "pending", "pendente" -> PENDING;
            case "accepted", "aceito" -> ACCEPTED;
            case "canceled", "cancelado" -> CANCELED;
            case "completed", "completado" -> COMPLETED;
            default -> PENDING;
        };
    }
}

