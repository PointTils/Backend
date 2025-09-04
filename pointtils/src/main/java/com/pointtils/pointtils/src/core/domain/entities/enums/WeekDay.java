package com.pointtils.pointtils.src.core.domain.entities.enums;

public enum WeekDay {
    MON, TUE, WEN, THU, FRI, SAT, SUN;
    
    public static WeekDay fromString(String value) {
        if (value == null) return MON;
        return switch (value.toLowerCase()) {
            case "monday" -> MON;
            case "tuesday" -> TUE;
            case "wednesday" -> WEN;
            case "thursday" -> THU;
            case "friday" -> FRI;
            case "saturday" -> SAT;
            case "sunday" -> SUN;
            default -> MON;
        };
    }
}
