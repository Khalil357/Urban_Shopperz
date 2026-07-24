package com.urbanshopper.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_TTL_SECONDS = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 3;
    private static final long RATE_LIMIT_TTL = 3600; // 1 hour
    private static final String RATE_LIMIT_PREFIX = "otp_rate:";
    private static final int MAX_REQUESTS_PER_HOUR = 3;
    private final SecureRandom random = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    public OtpResult generateOtp(String phone) {
        // Rate limiting
        String rateKey = RATE_LIMIT_PREFIX + phone;
        Long attempts = redisTemplate.opsForValue().increment(rateKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(rateKey, RATE_LIMIT_TTL, TimeUnit.SECONDS);
        }
        if (attempts != null && attempts > MAX_REQUESTS_PER_HOUR) {
            return new OtpResult(false, null, "Too many requests. Try again later.");
        }

        String otp = String.format("%06d", random.nextInt(1000000));
        String key = OTP_PREFIX + phone;

        redisTemplate.opsForValue().set(key, otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);

        // In production, send via SMS gateway
        System.out.println("OTP for " + phone + ": " + otp);

        return new OtpResult(true, otp, null);
    }

    public boolean validateOtp(String phone, String otp) {
        String key = OTP_PREFIX + phone;
        String stored = redisTemplate.opsForValue().get(key);

        if (stored != null && stored.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    record OtpResult(boolean success, String otp, String error) {}
}
