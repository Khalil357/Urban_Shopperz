package com.urbanshopper.domain.user;

import com.urbanshopper.shared.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerDTO>> register(@Valid @RequestBody RegisterCustomerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(customerService.register(req)));
    }
    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getProfile(id)));
    }
}
