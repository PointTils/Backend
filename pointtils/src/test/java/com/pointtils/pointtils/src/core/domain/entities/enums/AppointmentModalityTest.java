package com.pointtils.pointtils.src.core.domain.entities.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppointmentModalityTest {

    @Test
    void shouldReturnOnlineFromNullValue() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionFromInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson("INVALID_MODALITY"));
    }

    @ParameterizedTest
    @CsvSource({
            "online, ONLINE",
            "remoto, ONLINE",
            "r, ONLINE",
            "ONLINE, ONLINE",
            "presencial, PERSONALLY",
            "personally, PERSONALLY",
            "p, PERSONALLY",
            "PERSONALLY, PERSONALLY"
    })
    void shouldGetAppointmentModalityFromStringValue(String input, String expected) {
        assertEquals(AppointmentModality.valueOf(expected), AppointmentModality.fromJson(input));
    }

    @Test
    void shouldHandleWhitespaceInInput() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("  online  "));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("\tpresencial\n"));
    }

    @Test
    void shouldReturnCorrectJsonValue() {
        assertEquals("ONLINE", AppointmentModality.ONLINE.toJson());
        assertEquals("PERSONALLY", AppointmentModality.PERSONALLY.toJson());
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldMaintainBackwardCompatibilityWithFromString() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromString("online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromString("presencial"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromString("personally"));
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromString("remoto"));
    }
}