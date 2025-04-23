package com.java.school.online_video_training.service;

import com.java.school.online_video_training.entity.User;

public interface EmailService {
	void sendVerificationEmail(String to, String subject, String text);
}
