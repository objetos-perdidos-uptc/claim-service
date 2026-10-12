package com.objetosperdidos.claimservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * Genera y valida OTP de 6 dígitos guardados en Redis con TTL.
 * Claves: otp:{claimId} -> código, otp:attempts:{claimId} -> intentos fallidos
 */
@Service
public class OtpService {

    private final StringRedisTemplate redis;
    private final SecureRandom random = new SecureRandom();
    private final long ttlMinutes;
    private final int maxAttempts;

    public OtpService(StringRedisTemplate redis,
                      @Value("${claims.otp.ttl-minutes}") long ttlMinutes,
                      @Value("${claims.otp.max-attempts}") int maxAttempts) {
        this.redis = redis;
        this.ttlMinutes = ttlMinutes;
        this.maxAttempts = maxAttempts;
    }

    public String generate(String claimId) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        Duration ttl = Duration.ofMinutes(ttlMinutes);
        redis.opsForValue().set(key(claimId), code, ttl);
        redis.delete(attemptsKey(claimId));
        return code;
    }

    public enum Result { VALID, INVALID, EXPIRED, TOO_MANY_ATTEMPTS }

    public Result verify(String claimId, String code) {
        String attemptsRaw = redis.opsForValue().get(attemptsKey(claimId));
        int attempts = attemptsRaw == null ? 0 : Integer.parseInt(attemptsRaw);
        if (attempts >= maxAttempts) {
            redis.delete(key(claimId));
            return Result.TOO_MANY_ATTEMPTS;
        }

        String stored = redis.opsForValue().get(key(claimId));
        if (stored == null) return Result.EXPIRED;

        if (stored.equals(code)) {
            redis.delete(key(claimId));
            redis.delete(attemptsKey(claimId));
            return Result.VALID;
        }

        redis.opsForValue().increment(attemptsKey(claimId));
        redis.expire(attemptsKey(claimId), Duration.ofMinutes(ttlMinutes));
        return Result.INVALID;
    }

    private String key(String claimId) { return "otp:" + claimId; }
    private String attemptsKey(String claimId) { return "otp:attempts:" + claimId; }
}
