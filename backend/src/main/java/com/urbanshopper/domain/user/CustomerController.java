package com.urbanshopper.domain.user;

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
    public ResponseEntity<ApiResponse<CustomerDTO>> register(@Valid @RequestBody RegisterCustomerRequest request) {
        var customer = customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(customer));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> getProfile(@PathVariable UUID id) {
        var customer = customerService.getProfile(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @PatchMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        var customer = customerService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }
}

record ApiResponse<T>(boolean success, T data, ErrorDetail error) {
    static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }
    static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message, null));
    }
    static <T> ApiResponse<T> error(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message, details));
    }
}

record ErrorDetail(String code, String message, Object details) {}
