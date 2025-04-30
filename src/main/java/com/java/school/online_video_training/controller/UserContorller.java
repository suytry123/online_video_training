package com.java.school.online_video_training.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserContorller {
    
    private final UserService userService;
    private final LocalValidatorFactoryBean validator;
    private final ObjectMapper objectMapper;
    
    public UserContorller(UserService userService, LocalValidatorFactoryBean validator, ObjectMapper objectMapper) {
        this.userService = userService;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }
    
    @PostMapping("/registerForm")
    public ResponseEntity<?> register(@RequestBody String rawBody) {
        try {
            log.info("Raw request body: {}", rawBody);
            
            // Parse JSON to DTO
            UserRegistrationDTO registrationDTO = objectMapper.readValue(rawBody, UserRegistrationDTO.class);
            log.info("Parsed DTO: {}", registrationDTO);
            
            // Validate the DTO
            Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
            if (!violations.isEmpty()) {
                log.error("Validation errors: {}", violations);
                Map<String, String> errors = new HashMap<>();
                violations.forEach(violation -> 
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage())
                );
                return ResponseEntity.badRequest().body(errors);
            }
            
            // Process registration
            User user = userService.registerUserForm(registrationDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        String message = userService.verifyEmail(token);
        return ResponseEntity.ok(message);
    }
}