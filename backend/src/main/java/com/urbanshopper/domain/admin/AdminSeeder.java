package com.urbanshopper.domain.admin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Seeds the default admin user on startup if not exists.
 * Password: admin123
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder {

    private final AdminUserRepository adminUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void seed() {
        if (adminUserRepository.findByUsername("admin").isEmpty()) {
            var admin = AdminUser.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role("super_admin")
                .name("Super Admin")
                .build();
            adminUserRepository.saveAndFlush(admin);
            System.out.println("Default admin user seeded (admin / admin123)");
        }
    }
}
