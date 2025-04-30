package com.java.school.online_video_training.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String, List<String>> errors;
    private String timestamp;

    // Constructor for ApiException
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.errors = null;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Constructor for all fields
    public ErrorResponse(int status, String message, Map<String, List<String>> errors, String timestamp) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    // Static factory method for ApiException
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }

    // Static factory method for creating ErrorResponse with errors
    public static ErrorResponse of(int status, String message, Map<String, List<String>> errors) {
        return new ErrorResponse(status, message, errors, java.time.LocalDateTime.now().toString());
    }

    // Static factory method for creating ErrorResponse without errors
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, null, java.time.LocalDateTime.now().toString());
    }
}