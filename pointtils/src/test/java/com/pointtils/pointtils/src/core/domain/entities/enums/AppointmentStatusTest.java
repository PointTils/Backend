package com.pointtils.pointtils.src.core.domain.entities.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppointmentStatusTest {

    @Test
    void shouldReturnPendingFromNullValue() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionFromInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentStatus.fromJson("INVALID_STATUS"));
    }

    @ParameterizedTest
    @CsvSource({
            "pending, PENDING",
            "pendente, PENDING",
            "PENDING, PENDING",
            "accepted, ACCEPTED",
            "aceito, ACCEPTED",
            "ACCEPTED, ACCEPTED",
            "canceled, CANCELED",
            "cancelado, CANCELED",
            "CANCELED, CANCELED",
            "completed, COMPLETED",
            "completado, COMPLETED",
            "COMPLETED, COMPLETED"
    })
    void shouldGetAppointmentStatusFromStringValue(String input, String expected) {
        assertEquals(AppointmentStatus.valueOf(expected), AppointmentStatus.fromJson(input));
    }

    @Test
    void shouldHandleWhitespaceInInput() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("  pending  "));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("\taccepted\n"));
    }

    @Test
    void shouldReturnCorrectJsonValue() {
        assertEquals("PENDING", AppointmentStatus.PENDING.toJson());
        assertEquals("ACCEPTED", AppointmentStatus.ACCEPTED.toJson());
        assertEquals("CANCELED", AppointmentStatus.CANCELED.toJson());
        assertEquals("COMPLETED", AppointmentStatus.COMPLETED.toJson());
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldMaintainBackwardCompatibilityWithFromString() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromString("pending"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromString("accepted"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromString("canceled"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromString("completed"));
    }
}