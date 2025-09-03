package com.pointtils.pointtils.src.infrastructure.configs;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void primeiraFalhaDeLogin_DeveRegistrarAttempt() {
        String ip = "127.0.0.1";

        service.loginFailed(ip);

        assertFalse(service.isBlocked(ip), "Primeira falha não deve bloquear");
    }

    @Test
    void multiplasFalhasDeLogin_DeveIncrementarContador() {
        String ip = "192.168.0.1";

        for (int i = 0; i < 3; i++) {
            service.loginFailed(ip);
        }

        // ainda não atingiu o limite
        assertFalse(service.isBlocked(ip));
    }

    @Test
    void loginBemSucedido_DeveLimparTentativas() {
        String ip = "10.0.0.1";

        for (int i = 0; i < 3; i++) {
            service.loginFailed(ip);
        }

        service.loginSucceeded(ip);

        assertFalse(service.isBlocked(ip));
    }

    @Test
    void deveBloquearAposMaximoDeFalhas() {
        String ip = "200.200.200.200";

        for (int i = 0; i < 5; i++) {
            service.loginFailed(ip);
        }

        assertTrue(service.isBlocked(ip));
    }

    @Test
    void deveDesbloquearAposTempoExpirado() throws Exception {
        String ip = "8.8.8.8";

        for (int i = 0; i < 5; i++) {
            service.loginFailed(ip);
        }

        // acessa o Map interno "attempts" e força lastAttempt para 20 minutos atrás (em SEGUNDOS!)
        Field attemptsField = getAttemptsField();
        @SuppressWarnings("unchecked")
        Map<String, Object> attempts = (Map<String, Object>) getFieldValue(service, attemptsField);
        Object attempt = attempts.get(ip);

        // Agora ajustamos para segundos (porque a classe usa seconds)
        long vinteMinutosAtrasEmSegundos = Instant.now().minusSeconds(20 * 60).getEpochSecond();
        setLastAttempt(attempt, vinteMinutosAtrasEmSegundos);

        assertFalse(service.isBlocked(ip), "Deveria estar desbloqueado após o tempo de bloqueio expirar");
    }

    @Test
    void ipSemRegistrosNaoDeveEstarBloqueado() {
        String ip = "1.1.1.1";

        assertFalse(service.isBlocked(ip));
    }

    @SuppressWarnings("UseSpecificCatch")
    private Field getAttemptsField() {
        try {
            Field field = LoginAttemptService.class.getDeclaredField("attempts");
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private Object getFieldValue(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private void setLastAttempt(Object attempt, long newValueSeconds) {
        try {
            Field field = attempt.getClass().getDeclaredField("lastAttempt");
            field.setAccessible(true);
            // usar setLong para evitar auto-boxing/erros de tipo
            field.setLong(attempt, newValueSeconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
