package com.urbanshopper.domain.auth;

/**
 * OTP service interface for phone verification (A-003).
 * Implementations:
 * - RedisOtpService: production, uses Redis for OTP storage
 * - DevOtpService: development, uses in-memory store
 */
public interface OtpService {
    boolean generateOtp(String phone);
    boolean validateOtp(String phone, String otp);
}
