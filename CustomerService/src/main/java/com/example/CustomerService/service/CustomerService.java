package com.example.CustomerService.service;

import com.example.CustomerService.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    CustomerDto create(CustomerDto dto);

    CustomerDto update(Long id, CustomerDto dto);

    CustomerDto getById(Long id);

    List<CustomerDto> getAll();

    void delete(Long id);
}
