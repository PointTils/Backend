package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterModalityTest {

    @Test
    void shouldThrowIllegalArgumentExceptionFromNullValue() {
        assertThrows(IllegalArgumentException.class, () -> InterpreterModality.fromString(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionFromInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> InterpreterModality.fromString("PRESENCIAL"));
    }

    @ParameterizedTest
    @CsvSource({
            "ONLINE, ONLINE",
            "PERSONALLY, PERSONALLY",
            "ALL, ALL"
    })
    void shouldGetModalityFromStringValue(String input, String expected) {
        assertEquals(InterpreterModality.valueOf(expected), InterpreterModality.fromString(input));
    }
}