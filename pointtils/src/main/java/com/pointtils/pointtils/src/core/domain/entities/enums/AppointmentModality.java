package com.pointtils.pointtils.src.core.domain.entities.enums;

public enum AppointmentModality { 
    ONLINE, PERSONALLY;
    
    public static AppointmentModality fromString(String value) {
        if (value == null) return ONLINE;
        return switch (value.toLowerCase()) {
            case "presencial", "personally", "p" -> PERSONALLY;
            case "online", "remoto" -> ONLINE;
            default -> ONLINE;
        };
    }
}