package com.java.school.online_video_training.service.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.config.security.JwtUtils;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.Role;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.UserMapper;
import com.java.school.online_video_training.repository.RoleRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.UserValidationService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper mapper;
	private final JwtUtils jwtUtils;
	private final UserValidationService userValidationService;

	@Override
	public Optional<AuthUser> findUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", username));

		AuthUser authUser = AuthUser.builder().username(user.getUsername()).password(user.getPassword())
				.authorities(getAuthorities(user.getRoles())).accountNonExpired(user.isAccountNonExpired())
				.accountNonLocked(user.isAccountNonLocked()).credentialsNonExpired(user.isCredentialsNonExpired())
				.enabled(user.isEnabled()).build();
		return Optional.ofNullable(authUser);
	}

	public Set<SimpleGrantedAuthority> getAuthorities(Set<Role> roles) {
		Set<SimpleGrantedAuthority> authorities1 = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toSet());
		Set<SimpleGrantedAuthority> authorities = roles.stream().flatMap(role -> toStream(role))
				.collect(Collectors.toSet());
		authorities.addAll(authorities1);
		return authorities;
	}

	private Stream<SimpleGrantedAuthority> toStream(Role role) {
		return role.getPermissions().stream().map(permiision -> new SimpleGrantedAuthority(permiision.getName()));
	}

	@Override
	@Transactional
	public User applyForAuthor(UserRegistrationDTO registrationDTO) {
		log.info("Processing author application for user: {}", registrationDTO.getUsername());

		// Find existing user
		User user = userRepository.findByUsername(registrationDTO.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Check if user is already an author
//		if (user.isAuthor()) {
//			throw new ApiException(HttpStatus.BAD_REQUEST, "User is already an author");
//		}
		if (user.getIsAuthor()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "User is already an author");
		}

		// Store temporary data for admin approval
		user.setTempGender(registrationDTO.getGender());
		user.setTempPhoneNumber(registrationDTO.getPhoneNumber());
		user.setTempEducation(registrationDTO.getEducation());
		user.setTempAddress(registrationDTO.getAddress());
		user.setTempAuthorBio(registrationDTO.getAuthorBio());
		user.setTempExpertise(registrationDTO.getAuthorExpertise());

		// Set application status
		user.setAuthorApprovalRequested(true);
		user.setAuthorApprovalStatus("PENDING");
		//user.setAuthor(false);
		user.setIsAuthor(false);
		user.setAuthorApproved(false);

		// Generate tokens for admin approval
		String approveToken = jwtUtils.generateJwtToken(user.getEmail() + "_APPROVE");
		String rejectToken = jwtUtils.generateJwtToken(user.getEmail() + "_REJECT");

		// Store tokens
		user.setApproveToken(approveToken);
		user.setRejectToken(rejectToken);

		// Send admin notification email
		try {
			String adminEmail = "Boysoy331@gmail.com";
			String subject = "ALERT: New Author Application Requires Your Attention";

			String text = String.format("""
					<html>
					<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
						<h2 style="color: #333; text-align: center;">New Author Application</h2>

						<p>Dear Admin,</p>

						<p>A user has applied to become an author. Please review the details below:</p>

						<div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
							<p style="margin: 5px 0;"><strong>Username:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Email:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Gender:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Phone:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Education:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Address:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Current Role:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Bio:</strong> %s</p>
							<p style="margin: 5px 0;"><strong>Expertise:</strong> %s</p>
						</div>

						<div style="text-align: center; margin: 30px 0;">
							<a href="%s" style="
								background-color: #4CAF50;
								color: white;
								padding: 12px 25px;
								text-decoration: none;
								border-radius: 5px;
								margin-right: 10px;
								font-weight: bold;
								display: inline-block;
							">APPROVE</a>

							<a href="%s" style="
								background-color: #f44336;
								color: white;
								padding: 12px 25px;
								text-decoration: none;
								border-radius: 5px;
								font-weight: bold;
								display: inline-block;
							">REJECT</a>
						</div>

						<p style="color: #666; font-size: 12px; text-align: center;">
							Note: This is an automated message. Please do not reply.
						</p>

						<p style="text-align: center;">
							Best regards,<br>
							Your Application Team
						</p>
					</body>
					</html>
					""", user.getUsername(), user.getEmail(), registrationDTO.getGender(),
					registrationDTO.getPhoneNumber(), registrationDTO.getEducation(), registrationDTO.getAddress(),
					user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")),
					registrationDTO.getAuthorBio(), registrationDTO.getAuthorExpertise(),
					"http://localhost:8080/api/user/author/approve?token=" + approveToken,
					"http://localhost:8080/api/user/author/reject?token=" + rejectToken);
			// Update email service to send HTML content
			emailService.sendVerificationEmail(adminEmail, subject, text, true);
			log.info("Admin notification email sent for author application: {}", user.getEmail());
		} catch (Exception e) {
			log.error("Failed to send admin notification email: {}", e.getMessage());
			throw new RuntimeException("Failed to send admin notification email", e);
		}

		// Save only the application status and tokens
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public String handleAuthorApproval(String token) {
		if (!jwtUtils.validateJwtToken(token)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
		}

		String email = jwtUtils.getUserNameFromJwtToken(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found"));

		if (token.endsWith("_APPROVE")) {
			// Admin approved - update user with temporary data
			user.setGender(user.getTempGender());
			user.setPhoneNumber(user.getTempPhoneNumber());
			user.setEducation(user.getTempEducation());
			user.setAddress(user.getTempAddress());
			user.setBio(user.getTempAuthorBio());
			user.setExpertise(user.getTempExpertise());

			// Clear temporary fields
			user.setTempGender(null);
			user.setTempPhoneNumber(null);
			user.setTempEducation(null);
			user.setTempAddress(null);
			user.setTempAuthorBio(null);
			user.setTempExpertise(null);

			// Update status
			user.setAuthorApprovalStatus("APPROVED");
			//user.setAuthor(true);
			user.setAuthorApprovalRequested(true);
			user.setAuthorApproved(true);

			// Add AUTHOR role
			Role authorRole = roleRepository.findByName("AUTHOR")
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));
			user.getRoles().add(authorRole);

			userRepository.save(user);
			return "Author application approved successfully";
		} else if (token.endsWith("_REJECT")) {
			// Admin rejected - clear all temporary data
			user.setTempGender(null);
			user.setTempPhoneNumber(null);
			user.setTempEducation(null);
			user.setTempAddress(null);
			user.setTempAuthorBio(null);
			user.setTempExpertise(null);

			// Update status
			user.setAuthorApprovalRequested(false);
			user.setAuthorApprovalStatus("REJECTED");
//			user.setAuthor(false);
			user.setIsAuthor(false);
			user.setAuthorApproved(false);

			userRepository.save(user);
			return "Author application rejected successfully";
		}

		throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid approval token");
	}

	/*
	@Override
	public String verifyEmail(String token) {
	    if (!jwtUtils.validateJwtToken(token)) {
	        throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
	    }

	    String email = jwtUtils.getUserNameFromJwtToken(token);
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found"));

	    // Extract the action (approve or reject) from the token's claims
	    Claims claims = jwtUtils.getClaimsFromToken(token);  // Get the claims from the token
	    String action = claims.get("action", String.class);  // Retrieve the 'action' claim from the token

	    // Check if this is an approval token
	    if ("approve".equals(action)) {
	        // Admin approved - now save the user data
	        user.setEnabled(true);
	        user.setAuthorApprovalStatus("APPROVED");
	        user.setBio(user.getTempAuthorBio());
	        user.setExpertise(user.getTempExpertise());
	        user.setTempAuthorBio(null);
	        user.setTempExpertise(null);
	        //user.setAuthor(true);
	        user.setIsAuthor(true);
	        user.setAuthorApproved(true);

	        // Add AUTHOR role
	        Role authorRole = roleRepository.findByName("AUTHOR")
	            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));
	        user.getRoles().add(authorRole);

	        userRepository.save(user);
	        return "User approved successfully and data stored in database";
	    } else if ("reject".equals(action)) {
	        // Admin rejected - don't save anything
	        return "User application rejected - no data stored in database";
	    }

	    return "Invalid token";  // If the action is neither 'approve' nor 'reject'
	}*/
	
	
	@Override
	public String verifyEmail(String token) {
		if (!jwtUtils.validateJwtToken(token)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
		}

		String email = jwtUtils.getUserNameFromJwtToken(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found"));

		// Check if this is an approval token
		if (token.endsWith("_APPROVE")) {
			// Admin approved - now save the user data
			user.setEnabled(true);
			user.setAuthorApprovalStatus("APPROVED");
			user.setBio(user.getTempAuthorBio());
			user.setExpertise(user.getTempExpertise());
			user.setTempAuthorBio(null);
			user.setTempExpertise(null);
			//user.setAuthor(true);
			user.setIsAuthor(true);
			user.setAuthorApproved(true);

			// Add AUTHOR role
			Role authorRole = roleRepository.findByName("AUTHOR")
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));
			user.getRoles().add(authorRole);

			userRepository.save(user);
			return "User approved successfully and data stored in database";
		} else if (token.endsWith("_REJECT")) {
			// Admin rejected - don't save anything
			return "User application rejected - no data stored in database";
		}

		return "Invalid token";
	}

	@Override
	public void sendAuthorConfirmationEmail(User user) {
		String subject = "ALERT: Author Application Approved";
		String body = String.format("ALERT: Author Application Approved\n\n" + "Dear %s,\n\n"
				+ "Congratulations! Your author application has been approved.\n"
				+ "You are now an author on our platform and can access author-related features.\n\n"
				+ "Your author details:\n" + "-------------------\n" + "Bio: %s\n" + "Expertise: %s\n\n"
				+ "Note: This is an automated message. Please do not reply.\n" + "Best regards,\n"
				+ "Your Application Team", user.getUsername(), user.getBio(), user.getExpertise());

		emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}

	@Override
	public void sendVerificationEmail(User user, String token) {
		//String baseUrl = System.getenv("BASE_URL") != null ? System.getenv("BASE_URL") : "http://localhost:8080";
		String baseUrl = "http://192.168.100.119:8080";
		String confirmationUrl = baseUrl + "/api/auth/verify?token=" + token;

		String subject = "ALERT: Email Verification Required";
		String body = String.format("ALERT: Email Verification Required\n\n" + "Dear %s,\n\n"
				+ "Please verify your email address by clicking the link below:\n\n" + "%s\n\n"
				+ "This link will expire in 24 hours.\n\n"
				+ "Note: This is an automated message. Please do not reply.\n" + "Best regards,\n"
				+ "Your Application Team", user.getUsername(), confirmationUrl);

		emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}

	@Override
	public String signupUser(SignupUser signupUser) {
		if (userRepository.existsByUsername(signupUser.getUsername())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken!");
		}

		if (userRepository.existsByEmail(signupUser.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already taken!");
		}

		// Create new user's account
		User user = new User(signupUser.getUsername(), signupUser.getEmail(),
		        passwordEncoder.encode(signupUser.getPassword()));

//		user.setRoles(Role.USER);
		// Fetch existing role "USER" from DB
	    Role userRole = roleRepository.findByName("USER")
	        .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "USER role not found"));

	    // Assign role to user
	    user.setRoles(Collections.singleton(userRole));
		userRepository.save(user);
		return jwtUtils.generateJwtToken(signupUser.getUsername());
	}
}
