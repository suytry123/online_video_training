package com.java.school.online_video_training.config.security;

import java.util.Optional;

import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

public interface UserService {
	Optional<AuthUser> findUserByUsername(String username);
	User registerUserForm(UserRegistrationDTO userDTO);
	String verifyEmail(String token);
	void sendVerificationEmail(User user, String token);
	void sendAuthorConfirmationEmail(User user);
}
