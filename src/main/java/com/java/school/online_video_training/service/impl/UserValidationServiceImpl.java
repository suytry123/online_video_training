package com.java.school.online_video_training.service.impl;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.UserValidationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserValidationServiceImpl implements UserValidationService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void validateUserRegistration(UserRegistrationDTO registrationDTO) throws ValidationException {
        log.info("Validating user registration for email: {}", registrationDTO.getEmail());

        // Validate required fields for author application
        if (registrationDTO.isWantToBeAuthor()) {
            if (registrationDTO.getAuthorBio() == null || registrationDTO.getAuthorBio().trim().isEmpty()) {
                throw new ValidationException("Author bio is required when applying to be an author");
            }
            if (registrationDTO.getAuthorExpertise() == null || registrationDTO.getAuthorExpertise().trim().isEmpty()) {
                throw new ValidationException("Author expertise is required when applying to be an author");
            }
        }

        // Validate username format
        if (!registrationDTO.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }

        // Validate password strength
        if (!registrationDTO.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new ValidationException("Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace");
        }

        // Validate phone number format
        if (!registrationDTO.getPhoneNumber().matches("^\\+?[0-9]{10,15}$")) {
            throw new ValidationException("Please provide a valid phone number");
        }

        log.info("User registration validation successful for email: {}", registrationDTO.getEmail());
    }
}