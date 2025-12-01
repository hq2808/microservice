package com.example.common_service.exception.mapper;

import java.util.Map;

public class UniqueConstraintMapper {
    public static final Map<String, String> UNIQUE_CONSTRAINTS = Map.ofEntries(
            Map.entry("uk_customers_user_id", "User ID already exists"),
            Map.entry("email", "Email already exists"),
            Map.entry("phone_number", "Phone number already exists"),
            Map.entry("username", "Username already exists")
    );

    public static String resolve(String dbMsg) {
        return UNIQUE_CONSTRAINTS.entrySet().stream()
                .filter(e -> dbMsg.toLowerCase().contains(e.getKey().toLowerCase()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("DATA_INTEGRITY_VIOLATION");
    }
}
