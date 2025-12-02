package com.example.ProductService.security;

import com.example.common_security.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends CommonSecurityBaseConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final SecurityWhitelistProperties whitelistProperties;

    public SecurityConfig(SecurityWhitelistProperties whitelistProperties) {
        this.whitelistProperties = whitelistProperties;
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils(), whitelistProperties.getUrls());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() // Các request không whitelist phải authenticate
        );

        return super.defaultSecurityFilterChain(http, jwtAuthenticationFilter());
    }
}
