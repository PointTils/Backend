package com.pointtils.pointtils.src.application.dto;

import com.pointtils.pointtils.src.application.dto.requests.RefreshTokenRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenRequestDTOTest {

    @Test
    void shouldCreateRefreshTokenRequestDTOWithAllArgsConstructor() {
        // Given
        String refreshToken = "test-refresh-token";

        // When
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(refreshToken);

        // Then
        assertNotNull(dto);
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    void shouldCreateRefreshTokenRequestDTOWithNoArgsConstructor() {
        // When
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getRefreshToken());
    }


    @Test
    void shouldSetAndGetRefreshToken() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        String refreshToken = "test-refresh-token";

        // When
        dto.setRefreshToken(refreshToken);

        // Then
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    void shouldHandleNullRefreshToken() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(null);

        // Then
        assertNull(dto.getRefreshToken());
    }

    @Test
    void shouldHandleEmptyRefreshToken() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("");

        // Then
        assertEquals("", dto.getRefreshToken());
    }

    @Test
    void shouldGenerateToString() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("test-refresh-token");

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        // Basic toString verification - the exact format may vary
        assertTrue(toString.contains("RefreshTokenRequestDTO") || toString.contains("refreshToken"));
    }

    @Test
    void shouldNotBeEqualWithDifferentRefreshToken() {
        // Given
        RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO("test-refresh-token");
        RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("different-refresh-token");

        // Then
        // Default Object.equals() behavior - different objects are not equal
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithNull() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("test-refresh-token");

        // Then
        assertNotEquals(null, dto);
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        // Given
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("test-refresh-token");
        String differentObject = "not a dto";

        // Then
        assertNotEquals(dto, differentObject);
    }
}
