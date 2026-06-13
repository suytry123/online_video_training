package com.java.school.online_video_training.service;

import com.java.school.online_video_training.dto.AuthorApplicationDTO;
import com.java.school.online_video_training.exception.ValidationException;

public interface UserValidationService {
    void validateUserRegistration(AuthorApplicationDTO authorApplicationDTO) throws ValidationException;
}
