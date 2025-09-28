package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentModalityTest {

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionFromInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson("INVALID_MODALITY"));
    }

    @ParameterizedTest
    @CsvSource({
            "ONLINE, ONLINE",
            "PERSONALLY, PERSONALLY"
    })
    void shouldGetAppointmentModalityFromStringValue(String input, String expected) {
        assertEquals(AppointmentModality.valueOf(expected), AppointmentModality.fromJson(input));
    }

    @Test
    void shouldHandleWhitespaceInInput() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("  ONLINE  "));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("\tPERSONALLY\n"));
    }

    @Test
    void shouldReturnCorrectJsonValue() {
        assertEquals("ONLINE", AppointmentModality.ONLINE.toJson());
        assertEquals("PERSONALLY", AppointmentModality.PERSONALLY.toJson());
    }

    @Test
    void shouldMaintainBackwardCompatibilityWithFromJson() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("ONLINE"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("PERSONALLY"));
    }

    @Test
    void shouldHandleEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson(""));
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson("   "));
    }

    @Test
    void shouldHandleCaseInsensitiveInput() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("personally"));
    }

    @Test
    void shouldValidateAllEnumValues() {
        AppointmentModality[] allValues = AppointmentModality.values();
        assertEquals(2, allValues.length);

        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentModality.ONLINE));
        assertTrue(java.util.Arrays.asList(allValues).contains(AppointmentModality.PERSONALLY));
    }

    @Test
    void shouldThrowExceptionForRandomInvalidInputs() {
        String[] invalidInputs = {"INVALID", "wrong", "123", "null", "undefined", "hybrid"};

        for (String invalidInput : invalidInputs) {
            assertThrows(IllegalArgumentException.class,
                    () -> AppointmentModality.fromJson(invalidInput),
                    "Should throw exception for input: " + invalidInput);
        }
    }

    @Test
    void shouldReturnConsistentJsonValues() {
        for (AppointmentModality modality : AppointmentModality.values()) {
            String jsonValue = modality.toJson();
            assertNotNull(jsonValue);
            assertFalse(jsonValue.isEmpty());
            assertEquals(modality.name(), jsonValue);
        }
    }
}