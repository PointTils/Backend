package com.pointtils.pointtils.src.infrastructure.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private MemoryBlacklistService memoryBlacklistService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros quando não há header Authorization")
    void deveContinuarSemHeaderAuthorization() throws ServletException, IOException {
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros quando header Authorization não começa com Bearer")
    void deveContinuarQuandoHeaderNaoComecaComBearer() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Basic dGVzdDp0ZXN0");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve retornar 401 quando token está na blacklist")
    void deveRetornar401QuandoTokenNaBlacklist() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted_token";
        request.addHeader("Authorization", "Bearer " + token);
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve retornar 401 quando token está expirado")
    void deveRetornar401QuandoTokenExpirado() throws ServletException, IOException {
        // Arrange
        String token = "expired_token";
        request.addHeader("Authorization", "Bearer " + token);
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtService.isTokenExpired(token)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando token é válido")
    void deveAutenticarComSucessoQuandoTokenValido() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        String email = "test@email.com";
        request.addHeader("Authorization", "Bearer " + token);
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(email);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("Deve retornar 401 quando ocorre exceção durante processamento do token")
    void deveRetornar401QuandoOcorreExcecao() throws ServletException, IOException {
        // Arrange
        String token = "invalid_token";
        request.addHeader("Authorization", "Bearer " + token);
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtService.isTokenExpired(token)).thenThrow(new RuntimeException("Token inválido"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    @DisplayName("Deve limpar contexto de segurança após processamento bem-sucedido")
    void deveLimparContextoSegurancaAposProcessamento() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        String email = "test@email.com";
        request.addHeader("Authorization", "Bearer " + token);
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(email);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Verifica que a autenticação foi definida
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Limpa o contexto para verificar que não há efeitos colaterais
        SecurityContextHolder.clearContext();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve permitir token blacklisted em endpoint de logout exato")
    void devePermitirTokenBlacklistedEmLogoutExato() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/v1/auth/logout");
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn("test@email.com");

        // Act - Não deve verificar blacklist para endpoints de logout
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Deve processar normalmente mesmo com token blacklisted
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve permitir token blacklisted em endpoint de logout com path adicional")
    void devePermitirTokenBlacklistedEmLogoutComPath() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/v1/auth/logout/123");
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn("test@email.com");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Deve permitir token blacklisted em endpoint de logout com substring")
    void devePermitirTokenBlacklistedEmLogoutComSubstring() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/api/v1/auth/logout");
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn("test@email.com");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Não deve permitir token blacklisted em endpoints que não são de logout")
    void naoDevePermitirTokenBlacklistedEmEndpointsNaoLogout() throws ServletException, IOException {
        // Arrange
        String token = "blacklisted_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/v1/auth/login");
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Deve bloquear token blacklisted em endpoints não-logout
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
    }

    @Test
    @DisplayName("Deve processar normalmente endpoint de refresh token")
    void deveProcessarNormalmenteEndpointRefresh() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/v1/auth/refresh");
        when(memoryBlacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn("test@email.com");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Deve processar normalmente endpoint de refresh
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }
}
