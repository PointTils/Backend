package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterModalityTest {

    @Test
    void shouldGetAllFromNullValue() {
        assertEquals(InterpreterModality.ALL, InterpreterModality.fromString(null));
    }

    @ParameterizedTest
    @CsvSource({
            "online, ONLINE",
            "presencial, PERSONALLY",
            "ambos, ALL",
            "unknown, ALL"
    })
    void shouldGetModalityFromStringValue(String input, String expected) {
        assertEquals(InterpreterModality.valueOf(expected), InterpreterModality.fromString(input));
    }
}