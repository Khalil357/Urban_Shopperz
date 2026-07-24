package com.urbanshopper.domain.user;

import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerDTO register(RegisterCustomerRequest req) {
        if (customerRepository.existsByPhone(req.phone()))
            throw new BusinessException("DUPLICATE_PHONE", "Phone already registered");
        var saved = customerRepository.save(Customer.builder()
            .phone(req.phone()).name(req.name())
            .language(req.language() != null ? req.language() : "sw").build());
        return CustomerDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public CustomerDTO getProfile(UUID id) {
        return customerRepository.findById(id)
            .map(CustomerDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("NOT_FOUND", "Customer not found"));
    }

    @Transactional(readOnly = true)
    public CustomerDTO getByPhone(String phone) {
        return customerRepository.findByPhone(phone)
            .map(CustomerDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("NOT_FOUND", "Customer not found"));
    }
}
