package com.java.school.online_video_training.service;

import com.java.school.online_video_training.config.jwt.LoginRequest;
import com.java.school.online_video_training.config.jwt.LoginResponse;
import com.java.school.online_video_training.dto.SignupRequest;

public interface AuthService {
	String createUser(SignupRequest signupRequest);
//	String authenticateUser(LoginRequest loginRequest);
	LoginResponse authenticateUser(LoginRequest loginRequest);
}
