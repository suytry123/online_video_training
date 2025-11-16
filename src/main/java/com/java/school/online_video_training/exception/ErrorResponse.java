package com.java.school.online_video_training.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private int status;
    private Object data;
    private Map<String, Object> meta;
    private String path;
    private String timestamp;
    private String traceId;

    // Constructor for ApiException
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.success = false;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Constructor for all fields
    public ErrorResponse(boolean success, String message, String errorCode, int status, Object data,
            Map<String, Object> meta, String path, String timestamp, String traceId) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.data = data;
        this.meta = meta;
        this.path = path;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    // Static factory method for ApiException
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }

    // Static factory method for creating ErrorResponse with errors
    public static ErrorResponse of(int status, String message, Map<String, Object> errors) {
        return new ErrorResponse(false, message, null, status, null, errors, null,
                LocalDateTime.now().toString(), null);
    }

    // Static factory method for creating ErrorResponse without errors
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(false, message, null, status, null, null, null,
                LocalDateTime.now().toString(), null);
    }
}