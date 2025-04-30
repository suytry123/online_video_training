package com.java.school.online_video_training.service;

import javax.validation.ValidationException;

import com.java.school.online_video_training.dto.UserRegistrationDTO;

public interface UserValidationService {
    void validateUserRegistration(UserRegistrationDTO registrationDTO) throws ValidationException;
}
