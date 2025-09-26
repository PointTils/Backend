package com.pointtils.pointtils.src.core.domain.entities.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    void shouldMaintainBackwardCompatibilityWithFromJson() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("presencial"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("personally"));
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("remoto"));
    }

    @Test
    void shouldHandleEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson(""));
        assertThrows(IllegalArgumentException.class, () -> AppointmentModality.fromJson("   "));
    }    @Test
    void shouldHandleCaseInsensitiveInput() {
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("ONLINE"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("PERSONALLY"));
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("REMOTO"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("PRESENCIAL"));
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

    @Test
    void shouldHandleAllAliasesForPersonally() {
        String[] personallyAliases = {"personally", "presencial", "p", "PERSONALLY", "PRESENCIAL", "P"};
        
        for (String alias : personallyAliases) {
            assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson(alias),
                "Failed for alias: " + alias);
        }
    }

    @Test
    void shouldHandleAllAliasesForOnline() {
        String[] onlineAliases = {"online", "remoto", "r", "ONLINE", "REMOTO", "R"};
        
        for (String alias : onlineAliases) {
            assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson(alias),
                "Failed for alias: " + alias);
        }
    }
}