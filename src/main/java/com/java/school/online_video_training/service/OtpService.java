package com.java.school.online_video_training.service;

public interface OtpService {
	void sendOtpForEmail(String usernameOrEmailOrPhone);
	boolean verifyOtpForEmail(String otp, String newPassword);
	void sendOtpForPhone(String phoneNumber);
	boolean verifyOtpForPhone(String phoneNumber, String otp, String newPassword);
}
