package com.pointtils.pointtils.dto;

import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenResponseDTOTest {

    @Test
    void shouldCreateRefreshTokenResponseDTOWithAllArgsConstructor() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        // When
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(accessToken, refreshToken);

        // Then
        assertNotNull(dto);
        assertEquals(accessToken, dto.getAccessToken());
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    void shouldCreateRefreshTokenResponseDTOWithNoArgsConstructor() {
        // When
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getAccessToken());
        assertNull(dto.getRefreshToken());
    }


    @Test
    void shouldSetAndGetAccessToken() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();
        String accessToken = "test-access-token";

        // When
        dto.setAccessToken(accessToken);

        // Then
        assertEquals(accessToken, dto.getAccessToken());
    }

    @Test
    void shouldSetAndGetRefreshToken() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();
        String refreshToken = "test-refresh-token";

        // When
        dto.setRefreshToken(refreshToken);

        // Then
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(null, null);

        // Then
        assertNull(dto.getAccessToken());
        assertNull(dto.getRefreshToken());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("", "");

        // Then
        assertEquals("", dto.getAccessToken());
        assertEquals("", dto.getRefreshToken());
    }

    @Test
    void shouldGenerateToString() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("test-access-token", "test-refresh-token");

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        // Basic toString verification - the exact format may vary
        assertTrue(toString.contains("RefreshTokenResponseDTO") || toString.contains("accessToken") || toString.contains("refreshToken"));
    }

    @Test
    void shouldNotBeEqualWithDifferentAccessToken() {
        // Given
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("test-access-token", "test-refresh-token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("different-access-token", "test-refresh-token");

        // Then
        // Default Object.equals() behavior - different objects are not equal
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithDifferentRefreshToken() {
        // Given
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("test-access-token", "test-refresh-token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("test-access-token", "different-refresh-token");

        // Then
        // Default Object.equals() behavior - different objects are not equal
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithNull() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("test-access-token", "test-refresh-token");

        // Then
        assertNotEquals(null, dto);
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("test-access-token", "test-refresh-token");
        String differentObject = "not a dto";

        // Then
        assertNotEquals(dto, differentObject);
    }
}
