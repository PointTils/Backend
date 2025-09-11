package com.pointtils.pointtils.src.core.domain.entities.enums;

public enum InterpreterModality { 
    ONLINE, PERSONALLY, ALL;
    
    public static InterpreterModality fromString(String value) {
        if (value == null) return ALL;
        return switch (value.toLowerCase()) {
            case "presencial" -> PERSONALLY;
            case "online" -> ONLINE;
            case "ambos" -> ALL;
            default -> ALL;
        };
    }
}
