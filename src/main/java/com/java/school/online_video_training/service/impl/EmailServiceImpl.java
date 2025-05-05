package com.java.school.online_video_training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.service.EmailService;

import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${app.base-url}")
	private String baseUrl;

	@Override
	public void sendVerificationEmail(String to, String subject, String text) {
		sendVerificationEmail(to, subject, text, false);
	}

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

	@Override
	public void sendAuthorApprovalRequestEmail(User user) {
		try {
			String subject = "New Author Approval Request";
			String approveUrl = baseUrl + "/admin/approve-author/" + user.getId();
			String rejectUrl = baseUrl + "/admin/reject-author/" + user.getId();

			String text = String.format("""
					<!DOCTYPE html>
					<html>
					<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
						<div style="max-width: 600px; margin: 0 auto; padding: 20px;">
							<h2 style="color: #2c3e50; text-align: center;">New Author Application</h2>
							<div style="background-color: #f8f9fa; border-radius: 5px; padding: 20px; margin: 20px 0;">
								<p><strong>Username:</strong> %s</p>
								<p><strong>Email:</strong> %s</p>
								<p><strong>Bio:</strong> %s</p>
								<p><strong>Expertise:</strong> %s</p>
							</div>
							<div style="text-align: center; margin: 30px 0;">
								<a href="%s"
									style="display: inline-block;
									padding: 12px 40px;
									background-color: #28a745;
									color: white;
									text-decoration: none;
									border-radius: 5px;
									font-weight: bold;
									margin-right: 10px;">
									APPROVE
								</a>
								<a href="%s"
									style="display: inline-block;
									padding: 12px 40px;
									background-color: #dc3545;
									color: white;
									text-decoration: none;
									border-radius: 5px;
									font-weight: bold;">
									REJECT
								</a>
							</div>
							<p style="color: #666; font-size: 12px; margin-top: 30px;">
								Note: This is an automated message. Please do not reply.
							</p>
						</div>
					</body>
					</html>
					""", user.getUsername(), user.getEmail(), user.getBio(), user.getExpertise(), approveUrl,
					rejectUrl);

			sendVerificationEmail("Boysoy331@gmail.com", subject, text, true);
			log.info("Author approval request email sent to admin for user: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send author approval request email: {}", e.getMessage());
			throw new RuntimeException("Failed to send author approval request email", e);
		}
	}
	/*
	 * @Override public void sendAuthorApprovalRequestEmail(User user) { try {
	 * String subject = "New Author Approval Request"; String text =
	 * String.format(""" <!DOCTYPE html> <html> <body
	 * style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;"> <div
	 * style="max-width: 600px; margin: 0 auto; padding: 20px;"> <h2
	 * style="color: #2c3e50; text-align: center;">New Author Application</h2> <div
	 * style="background-color: #f8f9fa; border-radius: 5px; padding: 20px; margin: 20px 0;"
	 * > <p><strong>Username:</strong> %s</p> <p><strong>Email:</strong> %s</p>
	 * <p><strong>Bio:</strong> %s</p> <p><strong>Expertise:</strong> %s</p> </div>
	 * <p style="text-align: center;">Please review and take appropriate action.</p>
	 * </div> </body> </html> """, user.getUsername(), user.getEmail(),
	 * user.getBio(), user.getExpertise() );
	 * 
	 * sendVerificationEmail("Boysoy331@gmail.com", subject, text, true);
	 * log.info("Author approval request email sent to admin for user: {}",
	 * user.getEmail()); } catch (Exception e) {
	 * log.error("Failed to send author approval request email: {}",
	 * e.getMessage()); throw new
	 * RuntimeException("Failed to send author approval request email", e); } }
	 */

	@Override
	public void sendAuthorApprovalStatusEmail(User user, boolean approved) {
		try {
			String status = approved ? "approved" : "rejected";
			String subject = "Author Application Status Update";
			String text = String.format("""
					<!DOCTYPE html>
					<html>
					<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
						<div style="max-width: 600px; margin: 0 auto; padding: 20px;">
							<h2 style="color: #2c3e50; text-align: center;">Author Application Update</h2>
							<p>Dear %s,</p>
							<p>Your author application has been <strong>%s</strong>.</p>
							<p>%s</p>
							<p style="color: #666; font-size: 12px; margin-top: 30px;">
								Note: This is an automated message. Please do not reply.
							</p>
						</div>
					</body>
					</html>
					""", user.getUsername(), status, approved ? "Congratulations! You can now start creating content."
					: "Thank you for your interest. You can reapply in the future.");

			sendVerificationEmail(user.getEmail(), subject, text, true);
			log.info("Author approval status email sent to user: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send author approval status email: {}", e.getMessage());
			throw new RuntimeException("Failed to send author approval status email", e);
		}
	}
}

/*
 * package com.java.school.online_video_training.service.impl;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.mail.MailException; import
 * org.springframework.mail.SimpleMailMessage; import
 * org.springframework.mail.javamail.JavaMailSender; import
 * org.springframework.stereotype.Service;
 * 
 * import com.java.school.online_video_training.entity.User; import
 * com.java.school.online_video_training.service.EmailService;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Service public class EmailServiceImpl implements EmailService {
 * 
 * @Autowired private JavaMailSender mailSender;
 * 
 * @Value("${spring.mail.username}") private String fromEmail;
 * 
 * @Value("${app.base-url}") private String baseUrl;
 * 
 * public EmailServiceImpl() {
 * log.info("EmailServiceImpl initialized with baseUrl: {}", baseUrl); }
 * 
 * @Override public void sendVerificationEmail(String to, String subject, String
 * text) { sendVerificationEmail(to, subject, text, false); }
 * 
 * @Override public void sendVerificationEmail(String to, String subject, String
 * text, boolean isHtml) { SimpleMailMessage message = new SimpleMailMessage();
 * message.setTo(to); message.setSubject(subject); message.setText(text);
 * message.setFrom("noreply@javaschool.com");
 * 
 * try { mailSender.send(message); log.info("Verification email sent to: {}",
 * to); } catch (MailException e) {
 * log.error("Failed to send verification email to: {}", to, e); throw new
 * RuntimeException("Failed to send verification email", e); } }
 * 
 * @Override public void sendVerificationEmail(User user) { try {
 * log.info("Starting to send verification email to: {}", user.getEmail());
 * log.info("Using baseUrl: {}", baseUrl);
 * 
 * String subject = "ALERT: Email Verification Required"; String verificationUrl
 * = baseUrl + "/api/auth/verify?token=" + user.getVerificationToken();
 * 
 * String text = String.format("ALERT: Email Verification Required\n\n" +
 * "Dear %s,\n\n" +
 * "Please verify your email address by clicking the link below:\n\n" + "%s\n\n"
 * + "This link will expire in 24 hours.\n\n" +
 * "Note: This is an automated message. Please do not reply.\n" +
 * "Best regards,\n" + "Your Application Team", user.getUsername(),
 * verificationUrl);
 * 
 * sendVerificationEmail(user.getEmail(), subject, text); } catch (Exception e)
 * { log.error("Failed to send verification email to: {}", user.getEmail(), e);
 * throw new RuntimeException("Failed to send verification email: " +
 * e.getMessage(), e); } }
 * 
 * @Override public void sendAuthorApprovalRequestEmail(User user) { try {
 * String subject = "ALERT: New Author Application Requires Your Attention";
 * String text = String.format( "ALERT: New Author Application\n\n" +
 * "Dear Admin,\n\n" +
 * "A new user has applied to become an author. Your immediate attention is required.\n\n"
 * + "Application Details:\n" + "-------------------\n" + "Username: %s\n" +
 * "Email: %s\n" + "Address: %s\n" + "Registration Date: %s\n" + "Bio: %s\n" +
 * "Expertise: %s\n\n" +
 * "Note: This is an automated message. Please do not reply.\n" +
 * "Best regards,\n" + "Your Application Team", user.getUsername(),
 * user.getEmail(), user.getAddress(), user.getJoinDate(),
 * user.getTempAuthorBio(), user.getTempExpertise());
 * 
 * sendVerificationEmail("Boysoy331@gmail.com", subject, text); } catch
 * (Exception e) { log.error("Failed to send author approval request email: {}",
 * e.getMessage()); throw new
 * RuntimeException("Failed to send author approval request email: " +
 * e.getMessage(), e); } }
 * 
 * @Override public void sendAuthorApprovalStatusEmail(User user, boolean
 * approved) { try { String status = approved ? "approved" : "rejected"; String
 * subject = "ALERT: Author Application Status Update"; String text =
 * String.format( "ALERT: Author Application Status Update\n\n" + "Dear %s,\n\n"
 * + "Your author application has been %s.\n\n" + "%s\n\n" +
 * "Note: This is an automated message. Please do not reply.\n" +
 * "Best regards,\n" + "Your Application Team", user.getUsername(), status,
 * approved ? "Congratulations! You can now start creating content." :
 * "Thank you for your interest. You can reapply in the future.");
 * 
 * sendVerificationEmail(user.getEmail(), subject, text); } catch (Exception e)
 * { log.error("Failed to send author approval status email: {}",
 * e.getMessage()); throw new
 * RuntimeException("Failed to send author approval status email: " +
 * e.getMessage(), e); } } }
 */