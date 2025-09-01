package com.pointtils.pointtils.src.infrastructure.configs;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5;
    private final long BLOCK_TIME = 15 * 60;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String email) {
        Attempt attempt = attempts.getOrDefault(email, new Attempt(0, Instant.now().getEpochSecond()));
        attempt.count++;
        attempt.lastAttempt = Instant.now().getEpochSecond();
        attempts.put(email, attempt);
    }

    public void loginSucceeded(String email) {
        attempts.remove(email);
    }

    public boolean isBlocked(String email) {
        Attempt attempt = attempts.get(email);
        if (attempt == null) return false;

        long now = Instant.now().getEpochSecond();
        if (now - attempt.lastAttempt > BLOCK_TIME) {
            attempts.remove(email);
            return false;
        }

        return attempt.count >= MAX_ATTEMPTS;
    }

    private static class Attempt {
        int count;
        long lastAttempt;

        Attempt(int count, long lastAttempt) {
            this.count = count;
            this.lastAttempt = lastAttempt;
        }
    }
}