package com.java.school.online_video_training.service;

import com.java.school.online_video_training.entity.User;

public interface EmailService {
	void sendVerificationEmail(String to, String subject, String text);
	void sendVerificationEmail(String to, String subject, String text, boolean isHtml);
	void sendAuthorApprovalRequestEmail(User user);
    void sendAuthorApprovalStatusEmail(User user, boolean approved);
	void sendVerificationEmail(User user);
}
