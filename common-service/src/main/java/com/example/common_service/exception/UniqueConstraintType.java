package com.example.common_service.exception;

public enum UniqueConstraintType {
    USER_ID("uk_customers_user_id", "User id already exists"),
    EMAIL("email", "Email already exists"),
    PHONE("phone_number", "Phone number already exists"),
    USERNAME("username", "Username already exists");

    private final String constraintName;
    private final String message;

    UniqueConstraintType(String constraintName, String message) {
        this.constraintName = constraintName;
        this.message = message;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getMessage() {
        return message;
    }

    // Hàm tìm theo lỗi root message DB
    public static String resolveMessage(String rootMessage) {
        for (UniqueConstraintType type : values()) {
            if (rootMessage.contains(type.constraintName)) {
                return type.message;
            }
        }
        return "DATA_INTEGRITY_VIOLATION";
    }
}
