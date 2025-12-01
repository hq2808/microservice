package com.example.common_service.exception;

public interface ConstraintNameExtractor {
    String extractConstraintName(String dbMessage);
    String extractColumnName(String dbMessage);
}
