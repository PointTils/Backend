package com.pointtils.pointtils.src.infrastructure.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfigurationSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateCorsConfigurationSourceUsingReflection() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        // Then
        assertNotNull(corsConfigurationSource);
    }

    @Test
    void corsConfigurationShouldAllowAllOrigins() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // Then
        assertNotNull(corsConfiguration);
        assertNotNull(corsConfiguration.getAllowedOriginPatterns());
        assertTrue(corsConfiguration.getAllowedOriginPatterns().contains("*"));
    }

    @Test
    void corsConfigurationShouldAllowExpectedMethods() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // Then
        assertNotNull(corsConfiguration);
        assertNotNull(corsConfiguration.getAllowedMethods());
        assertTrue(corsConfiguration.getAllowedMethods().contains("GET"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("POST"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("PUT"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("DELETE"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("OPTIONS"));
    }

    @Test
    void corsConfigurationShouldAllowExpectedHeaders() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // Then
        assertNotNull(corsConfiguration);
        assertNotNull(corsConfiguration.getAllowedHeaders());
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Authorization"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Content-Type"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Accept"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Origin"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("X-Requested-With"));
    }

    @Test
    void corsConfigurationShouldAllowCredentials() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // Then
        assertNotNull(corsConfiguration);
        assertTrue(corsConfiguration.getAllowCredentials());
    }

    @Test
    void corsConfigurationShouldHaveCorrectMaxAge() throws Exception {
        // Given
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);

        // When - Use reflection to access the private method
        Method corsConfigurationSourceMethod = SecurityConfiguration.class.getDeclaredMethod("corsConfigurationSource");
        corsConfigurationSourceMethod.setAccessible(true);
        CorsConfigurationSource corsConfigurationSource = (CorsConfigurationSource) corsConfigurationSourceMethod.invoke(securityConfiguration);

        MockHttpServletRequest request = new MockHttpServletRequest();
        var corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // Then
        assertNotNull(corsConfiguration);
        assertEquals(3600L, corsConfiguration.getMaxAge());
    }

    @Test
    void constructorShouldAcceptJwtAuthenticationFilter() {
        // When & Then
        assertDoesNotThrow(() -> {
            new SecurityConfiguration(jwtAuthenticationFilter);
        });
    }
}
