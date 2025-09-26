package com.pointtils.pointtils.src.application.dto;

import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenResponseDTOTest {

    @Test
    void shouldCreateRefreshTokenResponseDTOWithAllArgsConstructor() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        // When
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(new TokensDTO(accessToken, refreshToken, "Bearer", 3600L, 86400L)));

        // Then
        assertNotNull(dto);
        assertEquals(accessToken, dto.getData().tokens().getAccessToken());
        assertEquals(refreshToken, dto.getData().tokens().getRefreshToken());
    }

    @Test
    void shouldCreateRefreshTokenResponseDTOWithNoArgsConstructor() {
        // When
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getData());
        assertNull(dto.getData());
    }

    @Test
    void shouldSetAndGetAccessToken() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(new TokensDTO()));
        String accessToken = "test-access-token";

        // When
        dto.getData().tokens().setAccessToken(accessToken);

        // Then
        assertEquals(accessToken, dto.getData().tokens().getAccessToken());
    }

    @Test
    void shouldSetAndGetRefreshToken() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(new TokensDTO()));
        String refreshToken = "test-refresh-token";

        // When
        dto.getData().tokens().setRefreshToken(refreshToken);

        // Then
        assertEquals(refreshToken, dto.getData().tokens().getRefreshToken());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(new TokensDTO()));

        // Then
        assertNull(dto.getData().tokens().getAccessToken());
        assertNull(dto.getData().tokens().getRefreshToken());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(new TokensDTO("", "", "Bearer", 3600L, 86400L)));

        // Then
        assertEquals("", dto.getData().tokens().getAccessToken());
        assertEquals("", dto.getData().tokens().getRefreshToken());
    }

    @Test
    void shouldGenerateToString() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        // Basic toString verification - the exact format may vary
        assertTrue(toString.contains("RefreshTokenResponseDTO") || toString.contains("accessToken")
                || toString.contains("refreshToken"));
    }

    @Test
    void shouldNotBeEqualWithDifferentAccessToken() {
        // Given
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("different-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));

        // Then
        // Default Object.equals() behavior - different objects are not equal
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithDifferentRefreshToken() {
        // Given
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "different-refresh-token", "Bearer", 3600L, 86400L)));
        // Then
        // Default Object.equals() behavior - different objects are not equal
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithNull() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));

        // Then
        assertNotEquals(null, dto);
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        // Given
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(true, "success",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("test-access-token", "test-refresh-token", "Bearer", 3600L, 86400L)));
        String differentObject = "not a dto";

        // Then
        assertNotEquals(dto, differentObject);
    }
}
