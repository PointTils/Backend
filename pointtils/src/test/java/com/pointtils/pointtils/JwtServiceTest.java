package com.pointtils.pointtils.src.infrastructure.configs;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
    jwtService = new JwtService();
    // Chave Base64 com pelo menos 32 bytes (256 bits)
    ReflectionTestUtils.setField(jwtService, "secretKey", "c2VjdXJldGVzdGtleXNlY3VyZXRlc3RrZXlzZWN1cmV0ZXN0a2V5c2VjdXJldGVzdGtleQ==");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 10000L); // 10s
    }

    @Test
    void testGenerateTokenAndValidate() {
        String token = jwtService.generateToken();
        assertNotNull(token);
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testExtractClaim() {
        String token = jwtService.generateToken();
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertNotNull(subject);
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        String token = jwtService.generateToken();
        Thread.sleep(20); // Wait a bit
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testGetExpirationTime() {
        assertEquals(10000L, jwtService.getExpirationTime());
    }
}
