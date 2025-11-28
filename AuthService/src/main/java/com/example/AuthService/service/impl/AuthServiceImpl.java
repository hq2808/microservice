package com.example.AuthService.service.impl;

import com.example.AuthService.model.User;
import com.example.AuthService.response.LoginResponse;
import com.example.AuthService.service.AuthService;
import com.example.AuthService.service.UserCacheService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserCacheService cacheService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String login(String username, String password) {
        User u = cacheService.getUser(username);
        if (u == null) throw new IllegalArgumentException("User not found");
        if (!encoder.matches(password, u.getPasswordHash())) throw new IllegalArgumentException("Invalid password");
        return generateToken(username);
    }

    @Async("threadPoolTask")
    @Override
    public CompletableFuture<LoginResponse> loginAsync(String username, String password) {
        // 1️⃣ Query user
        User u = cacheService.getUser(username);
        if (u == null) throw new IllegalArgumentException("User not found");

        // 2️⃣ Check password (CPU heavy)
        if (!encoder.matches(password, u.getPasswordHash())) {
            return CompletableFuture.completedFuture(new LoginResponse("Invalid password"));
        }
        // 3️⃣ Generate JWT
        String token = generateToken(username);

        // 4️⃣ Return result
        return CompletableFuture.completedFuture(new LoginResponse("Success", token));
    }

    private String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}