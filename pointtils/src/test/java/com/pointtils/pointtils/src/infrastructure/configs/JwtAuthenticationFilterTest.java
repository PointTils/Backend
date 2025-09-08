package com.pointtils.pointtils.src.infrastructure.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Method doFilterInternalMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        SecurityContextHolder.clearContext();
        // Get the protected method using reflection
        doFilterInternalMethod = JwtAuthenticationFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        doFilterInternalMethod.setAccessible(true);
    }

    @Test
    void shouldContinueFilterChainWhenNoAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenExpired(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenExpired(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsExpired() throws Exception {
        // Given
        String token = "validjwttoken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenExpired(token)).thenReturn(true);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("Unauthorized.");
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateWhenValidToken() throws Exception {
        // Given
        String token = "validjwttoken";
        String subject = "testuser";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(subject);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(subject, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenJwtServiceThrowsException() throws Exception {
        // Given
        String token = "invalidjwttoken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenExpired(token)).thenThrow(new RuntimeException("Invalid token"));
        when(response.getWriter()).thenReturn(printWriter);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("Unauthorized.");
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldExtractTokenCorrectlyFromBearerHeader() throws Exception {
        // Given
        String token = "actualjwttoken";
        String bearerToken = "Bearer " + token;
        String subject = "testuser";
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(subject);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        verify(jwtService).isTokenExpired(token);
        verify(jwtService).extractClaim(eq(token), any());
        verify(filterChain).doFilter(request, response);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(subject, authentication.getPrincipal());
    }

    @Test
    void shouldHandleEmptyBearerToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        when(jwtService.isTokenExpired("")).thenThrow(new RuntimeException("Empty token"));
        when(response.getWriter()).thenReturn(printWriter);

        // When
        doFilterInternalMethod.invoke(jwtAuthenticationFilter, request, response, filterChain);

        // Then
        // The empty token after "Bearer " will cause an exception in JWT parsing
        // which will be caught and handled in the exception block
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("Unauthorized.");
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
