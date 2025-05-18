package com.java.school.online_video_training.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
	
	@Value("${app.admin-email}")
	private String adminEmail;

	@Override
	public void sendVerificationEmail(String to, String subject, String text) {
		sendVerificationEmail(to, subject, text, false);
	}

	@Async
	@Override
	public void sendVerificationEmail(String to, String subject, String text, boolean isHtml) {
		try {
			if (isHtml) {
				// Send HTML email
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

				helper.setFrom(fromEmail);
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(text, true); // true indicates HTML content

				mailSender.send(message);
			} else {
				// Send plain text email
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(fromEmail);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(text);

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
			String verificationUrl = baseUrl + "/users/verify-email?token=" + user.getVerificationToken();

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

	/*
	 * @Override public void sendAuthorApprovalRequestEmail(User user) { try {
	 * String subject = "New Author Approval Request"; String approveUrl = baseUrl +
	 * "/admin/approve-author/" + user.getId(); String rejectUrl = baseUrl +
	 * "/admin/reject-author/" + user.getId();
	 * 
	 * String text = String.format(""" <!DOCTYPE html> <html> <body
	 * style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;"> <div
	 * style="max-width: 600px; margin: 0 auto; padding: 20px;"> <h2
	 * style="color: #2c3e50; text-align: center;">New Author Application</h2> <div
	 * style="background-color: #f8f9fa; border-radius: 5px; padding: 20px; margin: 20px 0;"
	 * > <p><strong>Username:</strong> %s</p> <p><strong>Email:</strong> %s</p>
	 * <p><strong>Bio:</strong> %s</p> <p><strong>Expertise:</strong> %s</p> </div>
	 * <div style="text-align: center; margin: 30px 0;"> <a href="%s"
	 * style="display: inline-block; padding: 12px 40px; background-color: #28a745;
	 * color: white; text-decoration: none; border-radius: 5px; font-weight: bold;
	 * margin-right: 10px;"> APPROVE </a> <a href="%s" style="display: inline-block;
	 * padding: 12px 40px; background-color: #dc3545; color: white; text-decoration:
	 * none; border-radius: 5px; font-weight: bold;"> REJECT </a> </div> <p
	 * style="color: #666; font-size: 12px; margin-top: 30px;"> Note: This is an
	 * automated message. Please do not reply. </p> </div> </body> </html> """,
	 * user.getUsername(), user.getEmail(), user.getBio(), user.getExpertise(),
	 * approveUrl, rejectUrl);
	 * 
	 * sendVerificationEmail("Boysoy331@gmail.com", subject, text, true);
	 * log.info("Author approval request email sent to admin for user: {}",
	 * user.getEmail()); } catch (Exception e) {
	 * log.error("Failed to send author approval request email: {}",
	 * e.getMessage()); throw new
	 * RuntimeException("Failed to send author approval request email", e); } }
	 */

	@Async
	@Override
	public void sendAuthorApprovalRequestEmail(User user) {
	    String approveUrl = baseUrl + "/api/user/author/approve?token=" + user.getApproveToken();
	    String rejectUrl = baseUrl + "/api/user/author/reject?token=" + user.getRejectToken();
	    
	    String emailBody = "<h2>New Author Application</h2>"
	        + "<p>Dear Admin,</p>"
	        + "<p>A user has applied to become an author. Please review the details below:</p>"
	        + "<div style='background:#f9f9f9;padding:10px;border-radius:5px;'>"
	        + "<b>Username:</b> " + user.getUsername() + "<br>"
	        + "<b>Email:</b> " + user.getEmail() + "<br>"
	        + "<b>Gender:</b> " + user.getGender() + "<br>"
	        + "<b>Phone:</b> " + user.getPhoneNumber() + "<br>"
	        + "<b>Education:</b> " + user.getEducation() + "<br>"
	        + "<b>Address:</b> " + user.getAddress() + "<br>"
	        + "<b>Current Role:</b> " + user.getRoles() + "<br>"
	        + "<b>Bio:</b> " + user.getBio() + "<br>"
	        + "<b>Expertise:</b> " + user.getExpertise() + "<br>"
	        + "</div><br>"
	        + "<a href=\"" + approveUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 24px;text-decoration:none;border-radius:5px;\">APPROVE</a>"
	        + "&nbsp;"
	        + "<a href=\"" + rejectUrl + "\" style=\"background-color:#f44336;color:white;padding:10px 24px;text-decoration:none;border-radius:5px;\">REJECT</a>"
	        + "<br><br><small>Note: This is an automated message. Please do not reply.</small>"
	        + "<br><br>Best regards,<br>Your Application Team";

	    // Now send this emailBody as HTML using your sendHtmlEmail method
	    sendHtmlEmail(adminEmail, "New Author Application", emailBody);
	}

	private void sendHtmlEmail(String to, String subject, String htmlBody) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true); // true = isHtml
			mailSender.send(message);
		} catch (MessagingException e) {
			// Handle exception (log or rethrow)
			e.printStackTrace();
		}
	}

	@Async
	@Override
	public void sendAuthorApprovalStatusEmail(User user, boolean approved) {
		try {
			String status = approved ? "approved" : "rejected";
			String subject = "Author Application Status Update";
			String details = String.format("""
				<div style="background-color: #f8f9fa; border-radius: 5px; padding: 20px; margin: 20px 0;">
					<p><strong>Username:</strong> %s</p>
					<p><strong>Email:</strong> %s</p>
					<p><strong>Gender:</strong> %s</p>
					<p><strong>Phone:</strong> %s</p>
					<p><strong>Education:</strong> %s</p>
					<p><strong>Address:</strong> %s</p>
					<p><strong>Bio:</strong> %s</p>
					<p><strong>Expertise:</strong> %s</p>
				</div>""",
				user.getUsername(),
				user.getEmail(),
				user.getGender(),
				user.getPhoneNumber(),
				user.getEducation(),
				user.getAddress(),
				user.getBio(),
				user.getExpertise()
			);
			String text = String.format("""
				<!DOCTYPE html>
				<html>
				<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
					<div style="max-width: 600px; margin: 0 auto; padding: 20px;">
						<h2 style="color: #2c3e50; text-align: center;">Author Application Update</h2>
						<p>Dear %s,</p>
						<p>Your author application has been <strong>%s</strong>.</p>
						%s
						<p>%s</p>
						<p style="color: #666; font-size: 12px; margin-top: 30px;">
							Note: This is an automated message. Please do not reply.
						</p>
					</div>
				</body>
				</html>
				""",
				user.getUsername(),
				status,
				details,
				approved ? "Congratulations! You can now start creating content." : "Thank you for your interest. You can reapply in the future."
			);
			   log.info("[EMAIL][USER ALERT] Preparing to send author approval status email to user: {} (approved: {})", user.getEmail(), approved);
		        log.debug("[EMAIL][USER ALERT] Email subject: {}", subject);
		        log.debug("[EMAIL][USER ALERT] Email body: {}", text);
		        sendVerificationEmail(user.getEmail(), subject, text, true);
		        log.info("[EMAIL][USER ALERT] Author approval status email sent to user: {} (approved: {})", user.getEmail(), approved);
		    } catch (Exception e) {
		        log.error("[EMAIL][USER ALERT] Failed to send author approval status email to user: {} (approved: {}) - {}", user.getEmail(), approved, e.getMessage(), e);
		        throw new RuntimeException("Failed to send author approval status email", e);
		    }
	}
	
	@Async
	@Override
	public void sendAdminActionConfirmation(User user, boolean approved) {
		String status = approved ? "APPROVED" : "REJECTED";
		String subject = "Admin Action Confirmation: Author Application " + status;
		String details = String.format(
			"""
			<div style="background-color: #f8f9fa; border-radius: 5px; padding: 20px; margin: 20px 0;">
				<p><strong>Username:</strong> %s</p>
				<p><strong>Email:</strong> %s</p>
				<p><strong>Gender:</strong> %s</p>
				<p><strong>Phone:</strong> %s</p>
				<p><strong>Education:</strong> %s</p>
				<p><strong>Address:</strong> %s</p>
				<p><strong>Bio:</strong> %s</p>
				<p><strong>Expertise:</strong> %s</p>
			</div>""",
			user.getUsername(),
			user.getEmail(),
			user.getGender(),
			user.getPhoneNumber(),
			user.getEducation(),
			user.getAddress(),
			user.getBio(),
			user.getExpertise()
		);
		String text = String.format(
			"""
			<!DOCTYPE html>
			<html>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
				<div style="max-width: 600px; margin: 0 auto; padding: 20px;">
					<h2 style="color: #2c3e50; text-align: center;">Admin Action Confirmation</h2>
					<p>You have <strong>%s</strong> the following author application:</p>
					%s
					<p style="color: #666; font-size: 12px; margin-top: 30px;">
						Note: This is an automated message. Please do not reply.
					</p>
				</div>
			</body>
			</html>
			""",
			status,
			details
		);
		sendVerificationEmail(adminEmail, subject, text, true);
		log.info("Admin action confirmation email sent to admin: {} for user: {}", adminEmail, user.getEmail());
	}

	// NOTE: Bounce/error messages like 'Mail Delivery Subsystem' are generated by the recipient's mail server (e.g., Gmail) and cannot be produced or controlled by this application. This application can only log send failures or notify the admin if sending fails at the SMTP level.
}