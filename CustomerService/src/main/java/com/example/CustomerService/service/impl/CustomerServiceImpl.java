package com.example.CustomerService.service.impl;

import com.example.CustomerService.dto.CustomerDto;
import com.example.CustomerService.entity.Customer;
import com.example.CustomerService.repository.CustomerRepository;
import com.example.CustomerService.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    private CustomerDto toDto(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phoneNumber(c.getPhoneNumber())
                .address(c.getAddress())
                .userId(c.getUserId())
                .build();
    }

    private Customer toEntity(CustomerDto c) {
        return Customer.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phoneNumber(c.getPhoneNumber())
                .address(c.getAddress())
                .status(1)
                .build();
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        Customer saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer ex = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        ex.setFullName(dto.getFullName());
        ex.setPhoneNumber(dto.getPhoneNumber());
        ex.setAddress(dto.getAddress());
        return toDto(repo.save(ex));
    }

    @Override
    public CustomerDto getById(Long id) {
        return repo.findById(id).map(this::toDto).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Override
    public List<CustomerDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Customer not found");
        repo.deleteById(id);
    }
}