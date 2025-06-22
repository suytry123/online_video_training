package com.java.school.online_video_training.service;

public interface OtpService {
	void sendOtp(String usernameOrEmailOrPhone);
	boolean verifyOtp(String otp, String newPassword);
}
