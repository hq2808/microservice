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
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .role(c.getRole())
                .build();
    }

    private Customer toEntity(CustomerDto d) {
        return Customer.builder()
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .email(d.getEmail())
                .phone(d.getPhone())
                .address(d.getAddress())
                .role(d.getRole() == null ? "USER" : d.getRole())
                .build();
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        l
        Customer saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer ex = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        ex.setFirstName(dto.getFirstName());
        ex.setLastName(dto.getLastName());
        ex.setPhone(dto.getPhone());
        ex.setAddress(dto.getAddress()); // do not update email by default — or validate uniqueness
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