package com.pointtils.pointtils.src.application.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryResetTokenService {
    
    private static final String RESET_TOKEN_PREFIX = "reset_token:";
    
    private final Map<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    /**
     * Gera um novo reset token para um usuário
     * @param email Email do usuário
     * @return Reset token gerado
     */
    public String generateResetToken(String email) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofHours(1)); // Token expira em 1 hora
        
        ResetTokenInfo tokenInfo = new ResetTokenInfo(email, expiry);
        resetTokens.put(RESET_TOKEN_PREFIX + token, tokenInfo);
        
        return token;
    }

    /**
     * Valida um reset token
     * @param token Token a ser validado
     * @return Email do usuário se o token for válido, null caso contrário
     */
    public String validateResetToken(String token) {
        ResetTokenInfo tokenInfo = resetTokens.get(RESET_TOKEN_PREFIX + token);
        if (tokenInfo == null) {
            return null;
        }
        
        // Remove tokens expirados
        if (Instant.now().isAfter(tokenInfo.getExpiry())) {
            resetTokens.remove(RESET_TOKEN_PREFIX + token);
            return null;
        }
        
        return tokenInfo.getEmail();
    }

    /**
     * Remove um reset token (após uso bem-sucedido)
     * @param token Token a ser removido
     */
    public void invalidateResetToken(String token) {
        resetTokens.remove(RESET_TOKEN_PREFIX + token);
    }
    
    /**
     * Limpa tokens expirados
     */
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        resetTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpiry()));
    }

    /**
     * Classe interna para armazenar informações do token
     */
    private static class ResetTokenInfo {
        private final String email;
        private final Instant expiry;

        public ResetTokenInfo(String email, Instant expiry) {
            this.email = email;
            this.expiry = expiry;
        }

        public String getEmail() {
            return email;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}
