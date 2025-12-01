package com.example.common_service.exception;

import org.springframework.stereotype.Component;

@Component
public class PostgresConstraintNameExtractor implements ConstraintNameExtractor {

    @Override
    public String extractConstraintName(String msg) {
        if (msg == null) return null;
        int idx1 = msg.indexOf("violates unique constraint");
        if (idx1 == -1) return null;

        int start = msg.indexOf("\"", idx1);
        int end = msg.indexOf("\"", start + 1);
        if (start == -1 || end == -1) return null;

        return msg.substring(start + 1, end);
    }

    @Override
    public String extractColumnName(String msg) {
        if (msg == null) return null;
        int idx1 = msg.indexOf("Key (");
        if (idx1 == -1) return null;

        int start = idx1 + "Key (".length();
        int end = msg.indexOf(")", start);
        if (end == -1) return null;

        return msg.substring(start, end); // column name
    }
}
