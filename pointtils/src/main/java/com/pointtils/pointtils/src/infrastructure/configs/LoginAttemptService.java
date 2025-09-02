package com.pointtils.pointtils.src.infrastructure.configs;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAXATTEMPTS = 5;
    private static final long BLOCKTIME = 15L * 60L;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String ip) {
        Attempt attempt = attempts.getOrDefault(ip, new Attempt(0, Instant.now().getEpochSecond()));
        attempt.count++;
        attempt.lastAttempt = Instant.now().getEpochSecond();
        attempts.put(ip, attempt);
    }

    public void loginSucceeded(String ip) {
        attempts.remove(ip);
    }

    public boolean isBlocked(String ip) {
        Attempt attempt = attempts.get(ip);
        if (attempt == null)
            return false;

        if (attempt.count >= MAXATTEMPTS) {
            if (System.currentTimeMillis() - attempt.lastAttempt > BLOCKTIME) {
                attempts.remove(ip);
                return false;
            }
            return true;
        }
        return false;
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