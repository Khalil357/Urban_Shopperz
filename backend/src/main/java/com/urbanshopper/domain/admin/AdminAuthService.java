package com.urbanshopper.domain.admin;

import com.urbanshopper.shared.exception.BusinessException;
import com.urbanshopper.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public AdminAuthDTO login(AdminLoginRequest req) {
        var admin = adminUserRepository.findByUsername(req.username())
            .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid username or password"));

        if (!passwordEncoder.matches(req.password(), admin.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        if (!"active".equals(admin.getStatus())) {
            throw new BusinessException("ACCOUNT_INACTIVE", "Admin account is not active");
        }

        admin.setLastLoginAt(Instant.now());
        adminUserRepository.save(admin);

        var token = jwtService.generateAccessToken(admin.getId(), "ADMIN_" + admin.getRole().toUpperCase());

        return AdminAuthDTO.builder()
            .token(token)
            .username(admin.getUsername())
            .role(admin.getRole())
            .name(admin.getName())
            .build();
    }
}
