package com.java.school.online_video_training.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private String message;
    private List<FieldErrorDTO> fieldErrors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class FieldErrorDTO {
        private String field;
        private String message;
    }

    public static ValidationErrorResponse fromBindingResult(List<ObjectError> errors) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setMessage("Validation failed");
        response.setFieldErrors(new ArrayList<>());
        
        for (ObjectError error : errors) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                response.getFieldErrors().add(response.new FieldErrorDTO(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
                ));
            } else {
                // Handle global errors (not tied to a specific field)
                response.getFieldErrors().add(response.new FieldErrorDTO(
                    "global",
                    error.getDefaultMessage()
                ));
            }
        }

        return response;
    }

    public static ValidationErrorResponse fromSingleError(String field, String message) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setMessage("Validation failed");
        response.setFieldErrors(new ArrayList<>());
        response.getFieldErrors().add(response.new FieldErrorDTO(field, message));
        return response;
    }

    public static ValidationErrorResponse fromMessage(String message) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setMessage(message);
        response.setFieldErrors(new ArrayList<>());
        return response;
    }
}
