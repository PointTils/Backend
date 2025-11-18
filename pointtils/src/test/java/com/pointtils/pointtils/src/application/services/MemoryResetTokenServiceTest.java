package com.pointtils.pointtils.src.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MemoryResetTokenServiceTest {

    private MemoryResetTokenService memoryResetTokenService;

    @BeforeEach
    void setUp() {
        memoryResetTokenService = new MemoryResetTokenService();
    }

    @Test
    @DisplayName("Deve gerar um reset token único para o usuário")
    void shouldGenerateResetToken() {
        String mockUUIDString = "123e4567-e89b-12d3-a456-426614174000";
        UUID mockUUID = UUID.fromString(mockUUIDString);
        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(mockUUID);

            assertEquals(mockUUIDString, memoryResetTokenService.generateResetToken("user@email.com"));
        }
    }

    @Test
    @DisplayName("Validacao de token deve retornar null se token nao estiver em memória")
    void shouldReturnNullIfHasNoDataOfTokenToValidate() {
        assertNull(memoryResetTokenService.validateResetToken("nonexistent-token"));
    }

    @Test
    @DisplayName("Validacao de token deve retornar null se token estiver expirado")
    void shouldReturnNullIfTokenIsExpired() {
        String token = memoryResetTokenService.generateResetToken("user@email.com");

        Instant mockInstant = Instant.now().plus(2, ChronoUnit.DAYS);
        try (MockedStatic<Instant> instantMockedStatic = Mockito.mockStatic(Instant.class)) {
            instantMockedStatic.when(Instant::now).thenReturn(mockInstant);

            assertNull(memoryResetTokenService.validateResetToken(token));
        }
    }

    @Test
    @DisplayName("Validacao de token deve retornar e-mail do usuário se token estiver válido")
    void shouldReturnEmailIfTokenIsValid() {
        String token = memoryResetTokenService.generateResetToken("user@email.com");
        assertEquals("user@email.com", memoryResetTokenService.validateResetToken(token));
    }

    @Test
    @DisplayName("Deve tornar token de reset inválido")
    void shouldInvalidateResetToken() {
        String token = memoryResetTokenService.generateResetToken("user@email.com");
        assertEquals("user@email.com", memoryResetTokenService.validateResetToken(token));

        memoryResetTokenService.invalidateResetToken(token);
        assertNull(memoryResetTokenService.validateResetToken(token));
    }
}
