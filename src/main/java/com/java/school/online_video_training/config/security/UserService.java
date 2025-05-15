package com.java.school.online_video_training.config.security;

import java.util.Optional;

import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

public interface UserService {
	Optional<AuthUser> findUserByUsername(String username);
	User applyForAuthor(UserRegistrationDTO userDTO);
	String verifyEmail(String token);
	void sendVerificationEmail(User user, String token);
	void sendAuthorConfirmationEmail(User user);
	String handleAuthorApproval(String token);
	String handleAuthorRejection(String token);
	//String approveAuthor(String token);
	//String rejectAuthor(String token);
	//for user signup
	String signupUser(SignupUser signupUser);
}
