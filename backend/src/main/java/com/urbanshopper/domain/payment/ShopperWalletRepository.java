package com.urbanshopper.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ShopperWalletRepository extends JpaRepository<ShopperWallet, UUID> {
    Optional<ShopperWallet> findByShopperId(UUID shopperId);
}
