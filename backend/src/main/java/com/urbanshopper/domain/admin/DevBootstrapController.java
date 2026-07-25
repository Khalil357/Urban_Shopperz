package com.urbanshopper.domain.admin;

import com.urbanshopper.domain.assignment.ShopperAvailability;
import com.urbanshopper.domain.assignment.ShopperAvailabilityRepository;
import com.urbanshopper.domain.user.Customer;
import com.urbanshopper.domain.user.CustomerRepository;
import com.urbanshopper.shared.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Development-only endpoint to bootstrap test data.
 */
@RestController
@RequestMapping("/api/v1/dev")
@RequiredArgsConstructor
public class DevBootstrapController {

    private final CustomerRepository customerRepository;
    private final ShopperAvailabilityRepository availabilityRepository;

    @PostMapping("/bootstrap")
    public ApiResponse<BootstrapResult> bootstrap() {
        // Register test shopper as a customer (no dedicated shopper entity yet)
        var shopper = customerRepository.save(Customer.builder()
            .phone("255700000001")
            .name("Juma Shopper")
            .status("active")
            .build());

        var zoneId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");

        // Set shopper online in Mikocheni
        availabilityRepository.findByShopperId(shopper.getId()).orElseGet(() ->
            availabilityRepository.save(ShopperAvailability.builder()
                .shopperId(shopper.getId())
                .status("online")
                .currentLat(BigDecimal.valueOf(-6.7765))
                .currentLng(BigDecimal.valueOf(39.2620))
                .currentZoneId(zoneId)
                .transportType("motorcycle")
                .heartbeatAt(Instant.now())
                .onlineAt(Instant.now())
                .build()));

        return ApiResponse.success(new BootstrapResult(
            shopper.getId(),
            shopper.getPhone(),
            "Shopper ready in Mikocheni zone"));
    }

    private record BootstrapResult(UUID shopperId, String shopperPhone, String message) {}
}
