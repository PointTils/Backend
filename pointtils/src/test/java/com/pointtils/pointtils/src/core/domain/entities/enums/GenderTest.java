package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenderTest {

    @Test
    void shouldThrowIllegalArgumentExceptionFromNullValue() {
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionFromInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString("TEST"));
    }

    @ParameterizedTest
    @CsvSource({
            "OTHERS, OTHERS",
            "FEMALE, FEMALE",
            "MALE, MALE"
    })
    void shouldGetGenderFromStringValue(String input, String expected) {
        assertEquals(Gender.valueOf(expected), Gender.fromString(input));
    }
}