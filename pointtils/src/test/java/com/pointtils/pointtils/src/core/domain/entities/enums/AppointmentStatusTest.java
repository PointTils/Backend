package com.pointtils.pointtils.src.core.domain.entities.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppointmentStatusTest {

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentStatus.fromJson(null));
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
    void shouldMaintainBackwardCompatibilityWithFromJson() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("pending"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("accepted"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromJson("canceled"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromJson("completed"));
    }

    @Test
    void shouldHandleEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentStatus.fromJson(""));
        assertThrows(IllegalArgumentException.class, () -> AppointmentStatus.fromJson("   "));
    }    @Test
    void shouldHandleCaseInsensitiveInput() {
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("PENDING"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("ACCEPTED"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromJson("CANCELED"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromJson("COMPLETED"));
    }

    @Test
    void shouldValidateAllEnumValues() {
        AppointmentStatus[] allValues = AppointmentStatus.values();
        assertEquals(4, allValues.length);
        
        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentStatus.PENDING));
        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentStatus.ACCEPTED));
        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentStatus.CANCELED));
        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentStatus.COMPLETED));
    }

    @Test
    void shouldThrowExceptionForRandomInvalidInputs() {
        String[] invalidInputs = {"INVALID", "wrong", "123", "null", "undefined"};
        
        for (String invalidInput : invalidInputs) {
            assertThrows(IllegalArgumentException.class, 
                () -> AppointmentStatus.fromJson(invalidInput),
                "Should throw exception for input: " + invalidInput);
        }
    }

    @Test
    void shouldReturnConsistentJsonValues() {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            String jsonValue = status.toJson();
            assertNotNull(jsonValue);
            assertFalse(jsonValue.isEmpty());
            assertEquals(status.name(), jsonValue);
        }
    }
}