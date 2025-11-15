package com.pointtils.pointtils.src.infrastructure.configs;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
@TestPropertySource(locations = "classpath:application.properties")
class JwtRefreshTokenTest {

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private FirebaseConfig firebaseConfig;

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken("testuser");
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void testTokenNotExpired() {
        String token = jwtService.generateToken("testuser");
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    void testRefreshTokenNotExpired() {
        String refreshToken = jwtService.generateRefreshToken("testuser");
        boolean isExpired = jwtService.isTokenExpired(refreshToken);
        assertFalse(isExpired);
    }
}
