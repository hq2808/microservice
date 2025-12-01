package com.example.AuthService.service;

import com.example.AuthService.dto.RegisterUserDto;

public interface UserService {
    boolean create(RegisterUserDto dto);

    void generateUsers();
}
