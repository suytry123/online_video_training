package com.java.school.online_video_training.service;

import com.java.school.online_video_training.entity.AuthorApplication;
import com.java.school.online_video_training.entity.User;

public interface EmailService {
	void sendUserVerificationEmail(User user);
	boolean verifyEmail(String token);
	void resendVerificationEmail(String email);
	void sendResetPasswordEmail(User user);
	void sendVerificationEmail(String to, String subject, String text);
	void sendVerificationEmail(String to, String subject, String text, boolean isHtml);
	void sendVerificationEmail(User user);
	void sendAuthorApprovalRequestEmail(AuthorApplication application);
	void sendAuthorApprovalStatusEmail(AuthorApplication application, boolean approved);
	void sendAdminActionConfirmation(AuthorApplication application, boolean approved);
	void sendOtp(String to, String otp);
}
