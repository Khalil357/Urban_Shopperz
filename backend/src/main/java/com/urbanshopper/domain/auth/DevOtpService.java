package com.urbanshopper.domain.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OTP service for development without Redis.
 * Auto-activated when StringRedisTemplate bean is absent.
 */
@Service
@ConditionalOnMissingBean(StringRedisTemplate.class)
public class DevOtpService implements OtpService {

    private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateEntry> rateStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private static final long OTP_TTL_SECONDS = 300;
    private static final int MAX_PER_HOUR = 10;

    @Override
    public boolean generateOtp(String phone) {
        var now = Instant.now();

        var rate = rateStore.compute(phone, (k, v) -> {
            if (v == null || v.resetAt().isBefore(now)) {
                return new RateEntry(1, now.plus(Duration.ofHours(1)));
            }
            return new RateEntry(v.count() + 1, v.resetAt());
        });
        if (rate.count() > MAX_PER_HOUR) return false;

        String otp = String.format("%06d", random.nextInt(1000000));
        store.put(phone, new OtpEntry(otp, now.plus(Duration.ofSeconds(OTP_TTL_SECONDS))));
        System.out.println("OTP for " + phone + ": " + otp);
        return true;
    }

    @Override
    public boolean validateOtp(String phone, String otp) {
        var entry = store.get(phone);
        if (entry == null || entry.expiresAt().isBefore(Instant.now())) return false;
        if (!entry.otp().equals(otp)) return false;
        store.remove(phone);
        return true;
    }

    private record OtpEntry(String otp, Instant expiresAt) {}
    private record RateEntry(int count, Instant resetAt) {}
}
