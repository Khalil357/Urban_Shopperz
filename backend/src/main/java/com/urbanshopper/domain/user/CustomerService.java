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
    public CustomerDTO register(RegisterCustomerRequest request) {
        if (customerRepository.existsByPhone(request.phone())) {
            throw new BusinessException("DUPLICATE_PHONE", "Phone number already registered");
        }

        var entity = Customer.builder()
            .phone(request.phone())
            .name(request.name())
            .language(request.language() != null ? request.language() : "sw")
            .build();

        var saved = customerRepository.save(entity);
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

    @Transactional
    public CustomerDTO updateProfile(UUID id, UpdateCustomerRequest request) {
        var customer = customerRepository.findById(id)
            .orElseThrow(() -> new BusinessException("NOT_FOUND", "Customer not found"));

        if (request.name() != null) customer.setName(request.name());
        if (request.language() != null) customer.setLanguage(request.language());

        var saved = customerRepository.save(customer);
        return CustomerDTO.fromEntity(saved);
    }
}
