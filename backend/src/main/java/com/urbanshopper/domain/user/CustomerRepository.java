package com.urbanshopper.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
