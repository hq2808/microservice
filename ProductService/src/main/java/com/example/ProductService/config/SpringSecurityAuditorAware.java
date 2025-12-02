package com.example.ProductService.config;

import com.example.common_security.security.SecurityContextUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityContextUtils.getUserId()); // userId từ JWT
    }
}
