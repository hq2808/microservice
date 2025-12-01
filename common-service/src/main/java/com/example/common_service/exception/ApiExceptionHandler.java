package com.example.common_service.exception;

import com.example.common_service.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Component
public class ApiExceptionHandler {

    private final MySQLConstraintNameExtractor mysqlExtractor;
    private final PostgresConstraintNameExtractor pgExtractor;

    public ApiExceptionHandler(MySQLConstraintNameExtractor mysqlExtractor,
                               PostgresConstraintNameExtractor pgExtractor) {
        this.mysqlExtractor = mysqlExtractor;
        this.pgExtractor = pgExtractor;
    }

    // Validation lỗi input (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Lỗi IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Lỗi chung / không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                "Unexpected error: " + ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // concurrency với các field UNIQUE như email, phone, username
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleUniqueConstraint(DataIntegrityViolationException ex) {
        Throwable root = ex.getRootCause();
        String msg = root != null ? root.getMessage() : ex.getMessage();

        ConstraintNameExtractor extractor = detectExtractor(root, msg);

        String columnName = extractor.extractColumnName(msg);
        if (columnName != null) {
            String friendlyName = toFriendlyColumnName(columnName);
            return ResponseEntity.badRequest().body(friendlyName + " already exists");
        }

        return ResponseEntity.badRequest().body("DATA_INTEGRITY_VIOLATION");
    }

    // Detect DB type: MySQL or PostgreSQL
    private ConstraintNameExtractor detectExtractor(Throwable root, String msg) {
        if (root != null) {
            String className = root.getClass().getName();
            if (className.startsWith("com.mysql.")) {
                return mysqlExtractor;
            } else if (className.startsWith("org.postgresql.")) {
                return pgExtractor;
            }
        }

        // fallback: parse message
        if (msg != null) {
            if (msg.contains("Duplicate entry")) return mysqlExtractor;
            if (msg.contains("violates unique constraint")) return pgExtractor;
        }

        // default
        return mysqlExtractor;
    }

    // Lấy tên constraint từ message DB
    private String extractConstraintName(String dbMessage) {
        if (dbMessage == null) return null;

        int idx = dbMessage.indexOf("for key");
        if (idx == -1) return null;

        int firstQuote = dbMessage.indexOf("'", idx);
        int secondQuote = dbMessage.indexOf("'", firstQuote + 1);
        if (firstQuote == -1 || secondQuote == -1) return null;

        String fullKey = dbMessage.substring(firstQuote + 1, secondQuote);

        // fullKey có thể là customers.uk_customers_email
        if (fullKey.contains(".")) {
            return fullKey.split("\\.")[1]; // lấy constraint name
        }
        return fullKey;
    }

    // Chuyển column_name → Column Name
    private String toFriendlyColumnName(String column) {
        if (column == null) return "";
        String[] parts = column.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1));
            if (i != parts.length - 1) sb.append(" ");
        }
        return sb.toString();
    }
}