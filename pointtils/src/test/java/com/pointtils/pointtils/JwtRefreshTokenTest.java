package com.pointtils.pointtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.pointtils.pointtils.src.infrastructure.configs.JwtService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class JwtRefreshTokenTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertTrue(!token.isEmpty());
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken("testuser");
        assertNotNull(refreshToken);
        assertTrue(!refreshToken.isEmpty());
    }

    @Test
    void testTokenNotExpired() {
        String token = jwtService.generateToken("testuser");
        boolean isExpired = jwtService.isTokenExpired(token);
        assertTrue(!isExpired);
    }

    @Test
    void testRefreshTokenNotExpired() {
        String refreshToken = jwtService.generateRefreshToken("testuser");
        boolean isExpired = jwtService.isTokenExpired(refreshToken);
        assertTrue(!isExpired);
    }
}
