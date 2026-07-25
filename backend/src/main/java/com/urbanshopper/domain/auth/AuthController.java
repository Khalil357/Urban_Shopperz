package com.urbanshopper.domain.auth;

import com.urbanshopper.domain.user.CustomerDTO;
import com.urbanshopper.domain.user.CustomerService;
import com.urbanshopper.shared.exception.ApiResponse;
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
    public ResponseEntity<?> requestOtp(@Valid @RequestBody OtpRequest req) {
        boolean sent = otpService.generateOtp(req.phone());
        if (!sent) return ResponseEntity.badRequest()
            .body(ApiResponse.error("RATE_LIMITED", "Too many requests. Try again later."));
        return ResponseEntity.ok(ApiResponse.success(new OtpResponse(true)));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        if (!otpService.validateOtp(req.phone(), req.otp()))
            return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_OTP", "Invalid or expired OTP"));
        try {
            var customer = customerService.getByPhone(req.phone());
            var accessToken = jwtService.generateAccessToken(
                java.util.UUID.fromString(customer.id()), "CUSTOMER");
            return ResponseEntity.ok(ApiResponse.success(new AuthResponse(
                accessToken, accessToken, 1800, customer)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(
                new OtpVerifiedResponse(true, req.phone())));
        }
    }
}
record OtpRequest(@NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone) {}
record VerifyOtpRequest(@NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone, @NotBlank String otp) {}
record OtpResponse(boolean otpSent) {}
record OtpVerifiedResponse(boolean verified, String phone) {}
record AuthResponse(String accessToken, String refreshToken, int expiresIn, CustomerDTO user) {}
