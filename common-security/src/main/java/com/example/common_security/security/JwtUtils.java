package com.example.common_security.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
public class JwtUtils {

    private final String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public JwtPayload extractPayload(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        JwtPayload payload = new JwtPayload();
        payload.setUserId(claims.getSubject());
        payload.setUsername(claims.get("username", String.class));
        payload.setEmail(claims.get("email", String.class));
        payload.setRoles((java.util.List<String>) claims.get("roles"));

        return payload;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parse(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}