package com.pointtils.pointtils.src.infrastructure.configs;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Primary
public class MemoryBlacklistService {
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        // Adiciona o token à blacklist com expiração de 1 hora
        blacklist.put(BLACKLIST_PREFIX + token, Instant.now().plus(Duration.ofHours(1)));
    }

    public boolean isBlacklisted(String token) {
        Instant expiration = blacklist.get(BLACKLIST_PREFIX + token);
        if (expiration == null) {
            return false;
        }
        
        // Remove tokens expirados
        if (Instant.now().isAfter(expiration)) {
            blacklist.remove(BLACKLIST_PREFIX + token);
            return false;
        }
        
        return true;
    }
    
    // Método para limpar tokens expirados (pode ser chamado periodicamente)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}
