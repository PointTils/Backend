package com.pointtils.pointtils.src.infrastructure.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    private JwtService jwtService;
    private static final String TEST_SECRET_KEY = "c2VjdXJldGVzdGtleXNlY3VyZXRlc3RrZXlzZWN1cmV0ZXN0a2V5c2VjdXJldGVzdGtleQ==";
    private static final long JWT_EXPIRATION = 60000L; // 1 minute
    private static final long REFRESH_EXPIRATION = 120000L; // 2 minutes

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void shouldGenerateValidToken() {
        // Given
        String subject = "testuser";

        // When
        String token = jwtService.generateToken(subject);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void shouldGenerateValidRefreshToken() {
        // Given
        String subject = "testuser";

        // When
        String refreshToken = jwtService.generateRefreshToken(subject);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(refreshToken.contains("."));
        assertFalse(jwtService.isTokenExpired(refreshToken));
    }

    @Test
    void shouldExtractSubjectFromToken() {
        // Given
        String subject = "testuser";
        String token = jwtService.generateToken(subject);

        // When
        String extractedSubject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals(subject, extractedSubject);
    }

    @Test
    void shouldExtractIssuedAtFromToken() {
        // Given
        String subject = "testuser";
        long beforeTokenGeneration = System.currentTimeMillis() - 1000; // 1 second buffer
        String token = jwtService.generateToken(subject);
        long afterTokenGeneration = System.currentTimeMillis() + 1000; // 1 second buffer

        // When
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Then
        assertNotNull(issuedAt);
        long issuedAtTime = issuedAt.getTime();
        assertTrue(issuedAtTime >= beforeTokenGeneration,
                "IssuedAt time " + issuedAtTime + " should be >= " + beforeTokenGeneration);
        assertTrue(issuedAtTime <= afterTokenGeneration,
                "IssuedAt time " + issuedAtTime + " should be <= " + afterTokenGeneration);
    }

    @Test
    void shouldExtractExpirationFromToken() {
        // Given
        String subject = "testuser";
        String token = jwtService.generateToken(subject);

        // When
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldReturnCorrectExpirationTime() {
        // When
        long expirationTime = jwtService.getExpirationTime();

        // Then
        assertEquals(JWT_EXPIRATION, expirationTime);
    }

    @Test
    void shouldReturnCorrectRefreshExpirationTime() {
        // When
        long refreshExpirationTime = jwtService.getRefreshExpirationTime();

        // Then
        assertEquals(REFRESH_EXPIRATION, refreshExpirationTime);
    }

    @Test
    void shouldDetectNonExpiredToken() {
        // Given
        String token = jwtService.generateToken("testuser");

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void shouldDetectExpiredToken() {
        // Given - Create a token that is already expired (negative expiration time)
        JwtService expiredJwtService = new JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(expiredJwtService, "jwtExpiration", -60000L); // Already expired (negative 1 minute)

        String expiredToken = expiredJwtService.generateToken("testuser");

        // When & Then - Expect an ExpiredJwtException to be thrown when checking expired token
        assertThrows(ExpiredJwtException.class, () -> {
            expiredJwtService.isTokenExpired(expiredToken);
        }, "Should throw ExpiredJwtException for expired token");
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThrows(MalformedJwtException.class, () -> {
            jwtService.isTokenExpired(invalidToken);
        });
    }

    @Test
    void shouldThrowExceptionForTokenWithWrongSignature() {
        // Given
        JwtService differentKeyService = new JwtService();
        ReflectionTestUtils.setField(differentKeyService, "secretKey", "ZGlmZmVyZW50a2V5ZGlmZmVyZW50a2V5ZGlmZmVyZW50a2V5ZGlmZmVyZW50a2V5");
        ReflectionTestUtils.setField(differentKeyService, "jwtExpiration", JWT_EXPIRATION);

        String tokenWithDifferentKey = differentKeyService.generateToken("testuser");

        // When & Then
        assertThrows(SignatureException.class, () -> {
            jwtService.extractClaim(tokenWithDifferentKey, Claims::getSubject);
        });
    }

    @Test
    void shouldGenerateDifferentTokensForSameSubject() throws InterruptedException {
        // Given
        String subject = "testuser";
        CountDownLatch latch = new CountDownLatch(1);

        // When
        String token1 = jwtService.generateToken(subject);
        latch.await(1, TimeUnit.SECONDS); // Wait for 1 second
        String token2 = jwtService.generateToken(subject);

        // Then
        assertNotEquals(token1, token2, "Tokens should be different even for same subject");
        assertEquals(subject, jwtService.extractClaim(token1, Claims::getSubject));
        assertEquals(subject, jwtService.extractClaim(token2, Claims::getSubject));
    }

    @Test
    void shouldHandleNullSubject() {
        // When & Then
        assertDoesNotThrow(() -> {
            String token = jwtService.generateToken(null);
            assertNotNull(token);
            assertNull(jwtService.extractClaim(token, Claims::getSubject));
        });
    }

    @Test
    void shouldHandleEmptySubject() {
        // Given
        String emptySubject = "";

        // When
        String token = jwtService.generateToken(emptySubject);
        String extractedSubject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertNotNull(token);
        // Empty string might be converted to null by JWT library, so we check for both cases
        assertTrue(extractedSubject == null || extractedSubject.equals(emptySubject));
    }

    @Test
    void refreshTokenShouldHaveLongerExpirationThanRegularToken() {
        // Given
        String subject = "testuser";

        // When
        String regularToken = jwtService.generateToken(subject);
        String refreshToken = jwtService.generateRefreshToken(subject);

        Date regularTokenExpiration = jwtService.extractClaim(regularToken, Claims::getExpiration);
        Date refreshTokenExpiration = jwtService.extractClaim(refreshToken, Claims::getExpiration);

        // Then
        assertTrue(refreshTokenExpiration.after(regularTokenExpiration));
    }
}
