package com.example.AuthService.service;

import com.example.AuthService.response.LoginResponse;

import java.util.concurrent.CompletableFuture;

public interface AuthService {
    String login(String username, String password);
    CompletableFuture<LoginResponse> loginAsync(String username, String password);
}