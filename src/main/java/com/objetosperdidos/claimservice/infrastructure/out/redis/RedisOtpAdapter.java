package com.objetosperdidos.claimservice.infrastructure.out.redis;

import com.objetosperdidos.claimservice.application.port.out.OtpPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;

@Component
public class RedisOtpAdapter implements OtpPort {

    private final StringRedisTemplate redis;
    private final SecureRandom random = new SecureRandom();
    private final long ttlMinutes;
    private final int maxAttempts;

    public RedisOtpAdapter(StringRedisTemplate redis,
                           @Value("${claims.otp.ttl-minutes}") long ttlMinutes,
                           @Value("${claims.otp.max-attempts}") int maxAttempts) {
        this.redis = redis;
        this.ttlMinutes = ttlMinutes;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String generate(String claimId) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        Duration ttl = Duration.ofMinutes(ttlMinutes);
        redis.opsForValue().set(otpKey(claimId), code, ttl);
        redis.delete(attemptsKey(claimId));
        return code;
    }

    @Override
    public VerificationResult verify(String claimId, String code) {
        String raw = redis.opsForValue().get(attemptsKey(claimId));
        int attempts = raw == null ? 0 : Integer.parseInt(raw);
        if (attempts >= maxAttempts) {
            redis.delete(otpKey(claimId));
            return VerificationResult.TOO_MANY_ATTEMPTS;
        }
        String stored = redis.opsForValue().get(otpKey(claimId));
        if (stored == null) return VerificationResult.EXPIRED;

        if (stored.equals(code)) {
            redis.delete(otpKey(claimId));
            redis.delete(attemptsKey(claimId));
            return VerificationResult.VALID;
        }
        redis.opsForValue().increment(attemptsKey(claimId));
        redis.expire(attemptsKey(claimId), Duration.ofMinutes(ttlMinutes));
        return VerificationResult.INVALID;
    }

    private String otpKey(String id)      { return "otp:" + id; }
    private String attemptsKey(String id) { return "otp:attempts:" + id; }
}