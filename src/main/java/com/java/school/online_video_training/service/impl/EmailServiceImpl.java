package com.java.school.online_video_training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.service.EmailService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmail;
	
	@Value("${app.base-url}")
	private String baseUrl;

	public EmailServiceImpl() {
		log.info("EmailServiceImpl initialized with baseUrl: {}", baseUrl);
	}

	@Override
	public void sendVerificationEmail(User user) {
		try {
			log.info("Using baseUrl: {}", baseUrl);
			String subject = "Email Verification - Online Video Training";
			String verificationUrl = baseUrl + "/users/verify-email?token=" + user.getVerificationToken();
			log.info("Generated verification URL: {}", verificationUrl);
			String text = String.format(
				"Dear %s,\n\n" +
				"Thank you for registering with Online Video Training!\n\n" +
				"Please click the link below to verify your email address:\n" +
				"%s\n\n" +
				"If you did not create an account, please ignore this email.\n\n" +
				"Best regards,\n" +
				"Online Video Training Team",
				user.getUsername(),
				verificationUrl
			);
			
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(user.getEmail());
			message.setSubject(subject);
			message.setText(text);
			
			mailSender.send(message);
			log.info("Verification email sent successfully to: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send verification email to: {}", user.getEmail(), e);
			throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void sendVerificationEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
			log.info("Email sent successfully to: {}", to);
		} catch (Exception e) {
			log.error("Failed to send email to: {}", to, e);
			throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
		}
	}

	@Override
	public void sendAuthorApprovalRequestEmail(User user) {
		try {
			String subject = "New Author Approval Request";
			String text = String.format(
				"Dear Admin,\n\n" +
				"A new author approval request has been submitted:\n\n" +
				"User: %s\n" +
				"Email: %s\n" +
				"Bio: %s\n" +
				"Expertise: %s\n\n" +
				"Please review and take appropriate action.\n\n" +
				"Best regards,\n" +
				"Your Application Team",
				user.getUsername(),
				user.getEmail(),
				user.getBio(),
				user.getExpertise()
			);
			
			sendVerificationEmail("Boysoy331@gmail.com", subject, text);
			log.info("Author approval request email sent to admin for user: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send author approval request email: {}", e.getMessage());
			throw new RuntimeException("Failed to send author approval request email: " + e.getMessage(), e);
		}
	}

	@Override
	public void sendAuthorApprovalStatusEmail(User user, boolean approved) {
		try {
			String status = approved ? "approved" : "rejected";
			String subject = "Author Application Status Update";
			String text = String.format(
				"Dear %s,\n\n" +
				"Your author application has been %s.\n\n" +
				"%s\n\n" +
				"Best regards,\n" +
				"Your Application Team",
				user.getUsername(),
				status,
				approved ? "Congratulations! You can now start creating content." : 
						  "Thank you for your interest. You can reapply in the future."
			);
			
			sendVerificationEmail(user.getEmail(), subject, text);
			log.info("Author approval status email sent to user: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send author approval status email: {}", e.getMessage());
			throw new RuntimeException("Failed to send author approval status email: " + e.getMessage(), e);
		}
	}
}
