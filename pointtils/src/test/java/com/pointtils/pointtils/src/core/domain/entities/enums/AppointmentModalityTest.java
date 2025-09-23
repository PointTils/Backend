package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentModalityTest {

    @Test
    void shouldGetOnlineFromNullValue() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromString(null));
    }

    @ParameterizedTest
    @CsvSource({
            "online, ONLINE",
            "remoto, ONLINE",
            "presencial, PERSONALLY",
            "personally, PERSONALLY",
            "unknown, ONLINE"
    })
    void shouldGetModalityFromStringValue(String input, String expected) {
        assertEquals(AppointmentModality.valueOf(expected), AppointmentModality.fromString(input));
    }
}