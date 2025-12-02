package com.example.common_security.security;

import lombok.Data;

import java.util.List;

@Data
public class JwtPayload {
    private String userId;
    private String username;
    private String email;
    private List<String> roles;
}