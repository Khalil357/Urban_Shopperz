package com.urbanshopper.domain.auth;

import com.urbanshopper.domain.user.CustomerDTO;
import com.urbanshopper.domain.user.CustomerService;
import com.urbanshopper.shared.exception.ApiResponse;
import com.urbanshopper.shared.exception.BusinessException;
import com.urbanshopper.shared.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final CustomerService customerService;
    private final JwtService jwtService;

    @PostMapping("/otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody OtpRequest request) {
        var result = otpService.generateOtp(request.phone());
        if (!result.success()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("RATE_LIMITED", result.error()));
        }
        return ResponseEntity.ok(ApiResponse.success(new OtpResponse(true)));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        if (!otpService.validateOtp(request.phone(), request.otp())) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_OTP", "Invalid or expired OTP"));
        }

        // Check if customer exists
        try {
            var customer = customerService.getByPhone(request.phone());
            var accessToken = jwtService.generateAccessToken(
                java.util.UUID.fromString(customer.id()), "CUSTOMER");
            var refreshToken = jwtService.generateRefreshToken(
                java.util.UUID.fromString(customer.id()));

            return ResponseEntity.ok(ApiResponse.success(new AuthResponse(
                accessToken, refreshToken, 1800, customer)));
        } catch (BusinessException e) {
            // Customer not registered yet — return verification success
            return ResponseEntity.ok(ApiResponse.success(new OtpVerifiedResponse(true, request.phone())));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            var claims = jwtService.validateRefreshToken(request.refreshToken());
            var userId = java.util.UUID.fromString(claims.getSubject());
            var role = claims.get("role", String.class);

            var newAccessToken = jwtService.generateAccessToken(userId, role);
            return ResponseEntity.ok(ApiResponse.success(new TokenResponse(newAccessToken, 1800)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_REFRESH_TOKEN", "Invalid or expired refresh token"));
        }
    }
}

record OtpRequest(@NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone) {}
record VerifyOtpRequest(@NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone, @NotBlank String otp) {}
record RefreshTokenRequest(@NotBlank String refreshToken) {}
record OtpResponse(boolean otpSent) {}
record OtpVerifiedResponse(boolean verified, String phone) {}
record AuthResponse(String accessToken, String refreshToken, int expiresIn, CustomerDTO user) {}
record TokenResponse(String accessToken, int expiresIn) {}
