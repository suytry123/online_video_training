package com.java.school.online_video_training.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.school.online_video_training.entity.AuthorApplication;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${app.admin-email}")
	private String adminEmail;
	
	@Value("${app.frontend-url}")
	private String frontendUrl;

	@Override
	public void sendVerificationEmail(String to, String subject, String text) {
		sendVerificationEmail(to, subject, text, false);
	}

	/*@Async
	@Override
	public void sendUserVerificationEmail(User user) {

		String verifyUrl = baseUrl + "/verify-email?token=" + user.getVerificationToken();

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(user.getEmail());

		message.setSubject("Verify your account");

		message.setText("Please click the link below to verify your account:\n\n" + verifyUrl);

		try {
			mailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send verification email", e);
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send verification email");
		}
	}*/
	
	@Async
	@Override
	public void sendUserVerificationEmail(User user) {

		String verifyUrl = baseUrl + "/api/email/verify-email?token=" + user.getVerificationToken();
		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(user.getEmail());
			helper.setFrom(fromEmail);
			helper.setSubject("Verify your LearnHub Account");

			String html = """
					<!DOCTYPE html>
					<html>
					<body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:20px;">

					    <div style="
					        max-width:600px;
					        margin:auto;
					        background:#ffffff;
					        padding:30px;
					        border-radius:10px;
					        box-shadow:0 2px 10px rgba(0,0,0,0.1);
					    ">

					        <h2 style="color:#198754;">
					            Welcome to LearnHub
					        </h2>

					        <p>Hello <strong>%s</strong>,</p>

					        <p>
					            Thank you for creating your LearnHub account.
					        </p>

					        <p>
					            Please verify your email address by clicking the button below.
					        </p>

					        <p style="text-align:center;margin:30px 0;">
					            <a href="%s"
					               style="
					                   background:#198754;
					                   color:white;
					                   padding:12px 24px;
					                   text-decoration:none;
					                   border-radius:6px;
					                   display:inline-block;
					                   font-weight:bold;
					               ">
					                Verify My Account
					            </a>
					        </p>

					        <p>
					            If the button does not work, copy and paste this link into your browser:
					        </p>

					        <p style="word-break:break-all;color:#0d6efd;">
					            %s
					        </p>

					        <hr>

					        <small style="color:gray;">
					            This verification link will expire in 24 hours.
					            If you did not create this account, you can safely ignore this email.
					        </small>

					        <hr>

					        <p style="font-size:12px;color:#888;">
					            LearnHub Team<br>
					            This is an automated email. Please do not reply.
					        </p>

					    </div>

					</body>
					</html>
					""".formatted(user.getUsername(), verifyUrl, verifyUrl);

			helper.setText("Please verify your account:\n" + verifyUrl, html);

			mailSender.send(message);

			log.info("Verification email sent to {}", user.getEmail());

		} catch (Exception e) {

			log.error("Failed to send verification email to {}", user.getEmail(), e);
		}
	}

	@Override
	@Transactional
	public boolean verifyEmail(String token) {

		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

		if (user.isEnabled()) {
			return true;
		}

		if (user.getVerificationTokenExpiry() == null
				|| user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {

			throw new ApiException(HttpStatus.BAD_REQUEST, "Verification link has expired");
		}

		user.setEnabled(true);
		user.setVerificationToken(null);
		user.setVerificationTokenExpiry(null);

		userRepository.save(user);

		return true;
	}

	@Transactional
	public void resendVerificationEmail(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

		if (user.isEnabled()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Account already verified");
		}

		String token = generateVerificationToken();

		user.setVerificationToken(token);
		user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

		userRepository.save(user);

		sendUserVerificationEmail(user);
	}

	private String generateVerificationToken() {
		SecureRandom random = new SecureRandom();

		byte[] bytes = new byte[32];

		random.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	@Async
	@Override
	public void sendResetPasswordEmail(User user) {

		String resetUrl = frontendUrl + "/reset-password?token=" + user.getResetPasswordToken();

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(user.getEmail());

		message.setSubject("Reset Password");

		message.setText("Click the link below to reset your password:\n\n" + resetUrl);

		try {
			mailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send reset email", e);
		}
	}

	@Async
	@Override
	public void sendVerificationEmail(String to, String subject, String content, boolean isHtml) {
		try {
			if (isHtml) {
				// Send HTML email
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

				helper.setFrom(fromEmail);
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(content, true); // true indicates HTML content

				mailSender.send(message);
			} else {
				// Send plain text email
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(fromEmail);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(content);

				mailSender.send(message);
			}
			log.info("Email sent successfully to: {}", to);
		} catch (Exception e) {
			log.error("Failed to send email to: {}", to, e);
			throw new RuntimeException("Failed to send email", e);
		}
	}

	@Async
	@Override
	public void sendVerificationEmail(User user) {
		try {
			log.info("Starting to send verification email to: {}", user.getEmail());

			String subject = "Email Verification - Online Video Training";
			String verificationUrl = baseUrl + "/api/users/verify-email?token=" + user.getVerificationToken();

			String text = String.format(
					"""
							<!DOCTYPE html>
							<html>
							<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
								<div style="max-width: 600px; margin: 0 auto; padding: 20px;">
									<h2 style="color: #2c3e50; text-align: center;">Welcome to Online Video Training!</h2>
									<p>Dear %s,</p>
									<p>Thank you for registering. Please click the button below to verify your email address:</p>
									<div style="text-align: center; margin: 30px 0;">
										<a href="%s"
											style="display: inline-block;
											padding: 12px 40px;
											background-color: #007bff;
											color: white;
											text-decoration: none;
											border-radius: 5px;
											font-weight: bold;">
											Verify Email
										</a>
									</div>
									<p>If you did not create an account, please ignore this email.</p>
									<p style="color: #666; font-size: 12px; margin-top: 30px;">
										Note: This is an automated message. Please do not reply.
									</p>
								</div>
							</body>
							</html>
							""",
					user.getUsername(), verificationUrl);

			sendVerificationEmail(user.getEmail(), subject, text, true);
			log.info("Verification email sent successfully to: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send verification email to: {}", user.getEmail(), e);
			throw new RuntimeException("Failed to send verification email", e);
		}
	}

	@Override
	@Async
	public void sendAuthorApprovalRequestEmail(AuthorApplication application) {

		User applicant = application.getApplicant();

		String reviewUrl = frontendUrl + "/admin/author-applications";
		
		String html = String.format("""
				<html>
				<body style="font-family: Arial, sans-serif; max-width: 700px; margin: auto;">

				    <h2>New Author Application</h2>

				    <p>A user has applied to become an author.</p>

				    <table style="border-collapse: collapse; width: 100%%;">
				        <tr>
				            <td><strong>Username</strong></td>
				            <td>%s</td>
				        </tr>

				        <tr>
				            <td><strong>Email</strong></td>
				            <td>%s</td>
				        </tr>

				        <tr>
				            <td><strong>Education</strong></td>
				            <td>%s</td>
				        </tr>

				        <tr>
				            <td><strong>Address</strong></td>
				            <td>%s</td>
				        </tr>

				        <tr>
				            <td><strong>Bio</strong></td>
				            <td>%s</td>
				        </tr>

				        <tr>
				            <td><strong>Expertise</strong></td>
				            <td>%s</td>
				        </tr>
				    </table>

				    <br/>

				    <a href="%s"
				       style="background:#007bff;
				              color:white;
				              padding:12px 24px;
				              text-decoration:none;
				              border-radius:4px;">
				       Review Application
				    </a>

				</body>
				</html>
				""", applicant.getUsername(), applicant.getEmail(), application.getEducation(),
				application.getAddress(), application.getBio(), application.getExpertise(), reviewUrl);

		sendVerificationEmail(adminEmail, "New Author Application", html, true);
	}

	@Override
	@Async
	public void sendAuthorApprovalStatusEmail(AuthorApplication application, boolean approved) {

		User applicant = application.getApplicant();

		String status = approved ? "APPROVED" : "REJECTED";

		String html = String.format("""
				<html>
				<body>

				    <h2>Author Application Result</h2>

				    <p>Dear %s,</p>

				    <p>Your author application has been <strong>%s</strong>.</p>

				    <hr>

				    <p><strong>Education:</strong> %s</p>
				    <p><strong>Address:</strong> %s</p>
				    <p><strong>Bio:</strong> %s</p>
				    <p><strong>Expertise:</strong> %s</p>

				    <hr>

				    <p>%s</p>

				</body>
				</html>
				""", applicant.getUsername(), status, application.getEducation(), application.getAddress(),
				application.getBio(), application.getExpertise(),
				approved ? "Congratulations! You can now create courses."
						: "Your application was not approved at this time.");

		sendVerificationEmail(applicant.getEmail(), "Author Application Status", html, true);
	}

	@Override
	@Async
	public void sendAdminActionConfirmation(AuthorApplication application, boolean approved) {

		User applicant = application.getApplicant();

		String status = approved ? "APPROVED" : "REJECTED";

		String html = String.format("""
				<html>
				<body>

				    <h2>Author Application %s</h2>

				    <p>The following application has been processed.</p>

				    <hr>

				    <p><strong>Username:</strong> %s</p>
				    <p><strong>Email:</strong> %s</p>
				    <p><strong>Education:</strong> %s</p>
				    <p><strong>Address:</strong> %s</p>
				    <p><strong>Bio:</strong> %s</p>
				    <p><strong>Expertise:</strong> %s</p>

				</body>
				</html>
				""", status, applicant.getUsername(), applicant.getEmail(), application.getEducation(),
				application.getAddress(), application.getBio(), application.getExpertise());

		sendVerificationEmail(adminEmail, "Author Application " + status, html, true);
	}
	
	@Async
	@Override
	public void sendOtp(String to, String otp) {
		String subject = "Your OTP Code";
		String message = "Your OTP code is: " + otp + ". It will expire in 10 minutes.";
		sendVerificationEmail(to, subject, message, false);
	}

	// NOTE: Bounce/error messages like 'Mail Delivery Subsystem' are generated by
	// the recipient's mail server (e.g., Gmail) and cannot be produced or
	// controlled by this application. This application can only log send failures
	// or notify the admin if sending fails at the SMTP level.
}