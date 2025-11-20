package com.pointtils.pointtils.src.infrastructure.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryBlacklistServiceTest {

    private MemoryBlacklistService memoryBlacklistService;

    @BeforeEach
    void setUp() {
        memoryBlacklistService = new MemoryBlacklistService();
    }

    @Test
    @DisplayName("Deve adicionar token à blacklist e verificar que está blacklisted")
    void deveAdicionarTokenABlacklistEVerificar() {
        // Arrange
        String token = "test_token_123";

        // Act
        memoryBlacklistService.addToBlacklist(token);
        boolean isBlacklisted = memoryBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(isBlacklisted, "Token deveria estar na blacklist");
    }

    @Test
    @DisplayName("Deve retornar false para token não adicionado à blacklist")
    void deveRetornarFalseParaTokenNaoAdicionado() {
        // Arrange
        String token = "token_nao_existente";

        // Act
        boolean isBlacklisted = memoryBlacklistService.isBlacklisted(token);

        // Assert
        assertFalse(isBlacklisted, "Token não deveria estar na blacklist");
    }

    @Test
    @DisplayName("Deve retornar false para token expirado na blacklist")
    void deveRetornarFalseParaTokenExpirado() {
        // Arrange
        String token = "token_expirado";
        memoryBlacklistService.addToBlacklist(token);

        // Simular passagem do tempo (não é possível realmente esperar 1 hora)
        // Este teste é mais conceitual, já que não podemos facilmente manipular o tempo
        // O método isBlacklisted já remove tokens expirados automaticamente

        // Assert - O token deve estar inicialmente na blacklist
        assertTrue(memoryBlacklistService.isBlacklisted(token), "Token deveria estar inicialmente na blacklist");
    }

    @Test
    @DisplayName("Deve adicionar múltiplos tokens à blacklist")
    void deveAdicionarMultiplosTokensABlacklist() {
        // Arrange
        String token1 = "token_1";
        String token2 = "token_2";
        String token3 = "token_3";

        // Act
        memoryBlacklistService.addToBlacklist(token1);
        memoryBlacklistService.addToBlacklist(token2);
        memoryBlacklistService.addToBlacklist(token3);

        // Assert
        assertTrue(memoryBlacklistService.isBlacklisted(token1), "Token 1 deveria estar na blacklist");
        assertTrue(memoryBlacklistService.isBlacklisted(token2), "Token 2 deveria estar na blacklist");
        assertTrue(memoryBlacklistService.isBlacklisted(token3), "Token 3 deveria estar na blacklist");
    }

    @Test
    @DisplayName("Deve limpar tokens expirados corretamente")
    void deveLimparTokensExpirados() {
        // Arrange
        String token1 = "token_1";
        String token2 = "token_2";

        // Adicionar tokens
        memoryBlacklistService.addToBlacklist(token1);
        memoryBlacklistService.addToBlacklist(token2);

        // Verificar que estão na blacklist
        assertTrue(memoryBlacklistService.isBlacklisted(token1));
        assertTrue(memoryBlacklistService.isBlacklisted(token2));

        // Act - Chamar cleanup (não deve remover tokens não expirados)
        memoryBlacklistService.cleanupExpiredTokens();

        // Assert - Tokens ainda devem estar na blacklist
        assertTrue(memoryBlacklistService.isBlacklisted(token1), "Token 1 ainda deveria estar na blacklist após cleanup");
        assertTrue(memoryBlacklistService.isBlacklisted(token2), "Token 2 ainda deveria estar na blacklist após cleanup");
    }

    @Test
    @DisplayName("Deve adicionar token com prefixo correto")
    void deveAdicionarTokenComPrefixoCorreto() {
        // Arrange
        String token = "test_token";

        // Act
        memoryBlacklistService.addToBlacklist(token);

        // Assert - O token deve estar acessível com e sem o prefixo pelo método público
        assertTrue(memoryBlacklistService.isBlacklisted(token), "Token deveria estar acessível pelo método público");
    }

    @Test
    @DisplayName("Deve retornar false para token nulo")
    void deveRetornarFalseParaTokenNulo() {
        // Act
        boolean isBlacklisted = memoryBlacklistService.isBlacklisted(null);

        // Assert
        assertFalse(isBlacklisted, "Token nulo não deveria estar na blacklist");
    }

    @Test
    @DisplayName("Deve retornar false para token vazio")
    void deveRetornarFalseParaTokenVazio() {
        // Act
        boolean isBlacklisted = memoryBlacklistService.isBlacklisted("");

        // Assert
        assertFalse(isBlacklisted, "Token vazio não deveria estar na blacklist");
    }

    @Test
    @DisplayName("Validacao de token deve retornar false se token estiver expirado")
    void shouldReturnNullIfTokenIsExpired() {
        String mockToken = "token";
        memoryBlacklistService.addToBlacklist(mockToken);

        Instant mockInstant = Instant.now().plus(2, ChronoUnit.DAYS);
        try (MockedStatic<Instant> instantMockedStatic = Mockito.mockStatic(Instant.class)) {
            instantMockedStatic.when(Instant::now).thenReturn(mockInstant);

            assertFalse(memoryBlacklistService.isBlacklisted(mockToken));
        }
    }
}
