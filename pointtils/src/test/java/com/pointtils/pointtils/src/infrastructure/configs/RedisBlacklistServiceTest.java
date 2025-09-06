package com.pointtils.pointtils.src.infrastructure.configs;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisBlacklistService redisBlacklistService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisBlacklistService = new RedisBlacklistService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Deve adicionar token na blacklist")
    void deveAdicionarTokenNaBlacklist() {
        String token = "meuToken";

        redisBlacklistService.addToBlacklist(token);

        verify(valueOperations, times(1))
                .set("blacklist:" + token, "true", Duration.ofHours(1));
    }

    @Test
    @DisplayName("Deve verificar se token está na blacklist")
    void deveRetornarTrueSeTokenEstiverNaBlacklist() {
        String token = "meuToken";

        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(true);

        boolean resultado = redisBlacklistService.isBlacklisted(token);

        assertTrue(resultado);
        verify(redisTemplate, times(1)).hasKey("blacklist:" + token);
    }

    @Test
    @DisplayName("Deve verificar se token não está na blacklist")
    void deveRetornarFalseSeTokenNaoEstiverNaBlacklist() {
        String token = "meuToken";

        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(false);

        boolean resultado = redisBlacklistService.isBlacklisted(token);

        assertFalse(resultado);
        verify(redisTemplate, times(1)).hasKey("blacklist:" + token);
    }
}