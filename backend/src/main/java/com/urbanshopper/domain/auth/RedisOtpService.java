package com.urbanshopper.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * Production OTP service using Redis for storage.
 * Active when Redis is available in the classpath/configuration.
 */
@Service
@ConditionalOnBean(StringRedisTemplate.class)
@RequiredArgsConstructor
public class RedisOtpService implements OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_TTL = 300;
    private static final String RATE_PREFIX = "otp_rate:";
    private static final int MAX_PER_HOUR = 3;
    private final SecureRandom random = new SecureRandom();
    private final StringRedisTemplate redis;

    @Override
    public boolean generateOtp(String phone) {
        String rateKey = RATE_PREFIX + phone;
        Long attempts = redis.opsForValue().increment(rateKey);
        if (attempts != null && attempts == 1) redis.expire(rateKey, Duration.ofHours(1));
        if (attempts != null && attempts > MAX_PER_HOUR) return false;

        String otp = String.format("%06d", random.nextInt(1000000));
        redis.opsForValue().set(OTP_PREFIX + phone, otp, Duration.ofSeconds(OTP_TTL));
        System.out.println("OTP for " + phone + ": " + otp);
        return true;
    }

    @Override
    public boolean validateOtp(String phone, String otp) {
        String stored = redis.opsForValue().get(OTP_PREFIX + phone);
        if (stored != null && stored.equals(otp)) {
            redis.delete(OTP_PREFIX + phone);
            return true;
        }
        return false;
    }
}
