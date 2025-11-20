package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtController jwtController;

    @Test
    @DisplayName("Deve gerar um token JWT público")
    void shouldGeneratePublicToken() {
        String mockToken = "token";
        when(jwtService.generateToken("user")).thenReturn(mockToken);

        ResponseEntity<String> response = jwtController.getPublicResource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockToken, response.getBody());
        verify(jwtService).generateToken("user");
    }

    @Test
    @DisplayName("Deve retornar mensagem de autenticado para recurso protegido")
    void shouldReturnAuthenticatedMessage() {
        ResponseEntity<String> response = jwtController.getProtectedResource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Authenticated.", response.getBody());
    }

    @Test
    @DisplayName("Deve gerar tokens JWT para um usuário")
    void shouldGenerateTokensForUser() {
        String username = "testUser";
        when(jwtService.generateToken(username)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(username)).thenReturn("refreshToken");

        ResponseEntity<RefreshTokenResponseDTO> response = jwtController.generateTokens(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Tokens gerados com sucesso", response.getBody().getMessage());

        verify(jwtService).generateToken(username);
        verify(jwtService).generateRefreshToken(username);
    }

    @Test
    @DisplayName("Deve fazer o refresh do token JWT com sucesso")
    void shouldRefreshTokenSuccessfully() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("validRefreshToken");
        String username = "testUser";
        when(jwtService.isTokenValid(request.getRefreshToken())).thenReturn(true);
        when(jwtService.getEmailFromToken(request.getRefreshToken())).thenReturn(username);
        when(jwtService.generateToken(username)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(username)).thenReturn("newRefreshToken");

        ResponseEntity<RefreshTokenResponseDTO> response = jwtController.refreshToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Tokens gerados com sucesso", response.getBody().getMessage());

        verify(jwtService).isTokenValid(request.getRefreshToken());
        verify(jwtService).getEmailFromToken(request.getRefreshToken());
        verify(jwtService).generateToken(username);
        verify(jwtService).generateRefreshToken(username);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Deve lançar exceção se token de refresh for vazio")
    void shouldThrowExceptionForBlankRefreshToken(String refreshToken) {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO(refreshToken);

        Exception exception = assertThrows(AuthenticationException.class, () -> jwtController.refreshToken(request));
        assertEquals("Refresh token não fornecido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se token de refresh for inválido")
    void shouldThrowExceptionForInvalidRefreshToken() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("invalidRefreshToken");
        when(jwtService.isTokenValid(request.getRefreshToken())).thenReturn(false);

        Exception exception = assertThrows(AuthenticationException.class, () -> jwtController.refreshToken(request));

        assertEquals("Refresh token inválido ou expirado", exception.getMessage());
        verify(jwtService).isTokenValid(request.getRefreshToken());
    }
}
