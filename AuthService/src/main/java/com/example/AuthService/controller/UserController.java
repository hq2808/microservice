package com.example.AuthService.controller;

import com.example.AuthService.dto.RegisterUserDto;
import com.example.AuthService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody RegisterUserDto dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    @GetMapping("/gen-users")
    public void generateUsers() {
        userService.generateUsers();
    }
}
