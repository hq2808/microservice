package com.example.common_service.exception;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MySQLConstraintNameExtractor implements ConstraintNameExtractor {

    @Override
    public String extractConstraintName(String dbMessage) {
        if (dbMessage == null) return null;
        int idx = dbMessage.indexOf("for key");
        if (idx == -1) return null;

        int firstQuote = dbMessage.indexOf("'", idx);
        int secondQuote = dbMessage.indexOf("'", firstQuote + 1);
        if (firstQuote == -1 || secondQuote == -1) return null;

        String fullKey = dbMessage.substring(firstQuote + 1, secondQuote);
        if (fullKey.contains(".")) {
            return fullKey.split("\\.")[1];
        }
        return fullKey;
    }

    @Override
    public String extractColumnName(String dbMessage) {
        // MySQL: constraint name chứa column, parse từ extractConstraintName
        String constraint = extractConstraintName(dbMessage);
        if (constraint == null) return null;
        String[] parts = constraint.split("_");
        return String.join("_", Arrays.copyOfRange(parts, 2, parts.length));
    }
}