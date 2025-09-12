package com.pointtils.pointtils.src.core.domain.entities.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenderTest {

    @Test
    void shouldGetOthersFromNullValue() {
        assertEquals(Gender.OTHERS, Gender.fromString(null));
    }

    @ParameterizedTest
    @CsvSource({
            "O, OTHERS",
            "F, FEMALE",
            "M, MALE",
            "X, OTHERS"
    })
    void shouldGetGenderFromStringValue(String input, String expected) {
        assertEquals(Gender.valueOf(expected), Gender.fromString(input));
    }
}
