package com.java.school.online_video_training.service.impl;

import java.util.HashSet;
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
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.Role;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.exception.UserAlreadyExistsException;
import com.java.school.online_video_training.mapper.UserMapper;
import com.java.school.online_video_training.repository.RoleRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.UserValidationService;

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
	public User registerUserForm(UserRegistrationDTO registrationDTO) {
		log.info("Registering new user with email: {}", registrationDTO.getEmail());

		// Validate user registration data
		userValidationService.validateUserRegistration(registrationDTO);

		// Check if user already exists
		if (userRepository.existsByEmail(registrationDTO.getEmail())) {
			log.warn("Registration failed - Email already exists: {}", registrationDTO.getEmail());
			throw new UserAlreadyExistsException("Email already registered");
		}

		if (userRepository.existsByUsername(registrationDTO.getUsername())) {
			log.warn("Registration failed - Username already exists: {}", registrationDTO.getUsername());
			throw new UserAlreadyExistsException("Username already taken");
		}

		// Create new user
		User user = new User();
		user.setUsername(registrationDTO.getUsername());
		user.setEmail(registrationDTO.getEmail());
		user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
		user.setGender(registrationDTO.getGender());
		user.setPhoneNumber(registrationDTO.getPhoneNumber());
		user.setEducation(registrationDTO.getEducation());
		user.setAddress(registrationDTO.getAddress());
		user.setEnabled(false); // User is disabled until email verification

		// Generate JWT token for email verification
		String verificationToken = jwtUtils.generateJwtToken(user.getEmail());
		user.setVerificationToken(verificationToken);

		// Set roles
		Set<Role> roles = new HashSet<>();

		// Get roles from registrationDTO
		if (registrationDTO.getRoleNames() != null && !registrationDTO.getRoleNames().isEmpty()) {
			for (String roleName : registrationDTO.getRoleNames()) {
				Role role = roleRepository.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
				roles.add(role);
			}
		} else {
			// If no roles specified, add default ROLE_USER
			Role userRole = roleRepository.findByName("ROLE_USER")
					.orElseThrow(() -> new ResourceNotFoundException("Default role ROLE_USER not found"));
			roles.add(userRole);
		}

		user.setRoles(roles);

		// Save user
		User savedUser = userRepository.save(user);
		log.info("User registered successfully with ID: {}", savedUser.getId());

		// Send verification email
		try {
			emailService.sendVerificationEmail(savedUser);
			log.info("Verification email sent to: {}", savedUser.getEmail());
		} catch (Exception e) {
			log.error("Failed to send verification email to: {}", savedUser.getEmail(), e);
		}

		return savedUser;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public User registerUserForm(UserRegistrationDTO
	 * registrationDTO) { log.info("Registering new user with email: {}",
	 * registrationDTO.getEmail());
	 * 
	 * // Validate user registration data
	 * userValidationService.validateUserRegistration(registrationDTO);
	 * 
	 * // Check if user already exists if
	 * (userRepository.existsByEmail(registrationDTO.getEmail())) {
	 * log.warn("Registration failed - Email already exists: {}",
	 * registrationDTO.getEmail()); throw new
	 * UserAlreadyExistsException("Email already registered"); }
	 * 
	 * if (userRepository.existsByUsername(registrationDTO.getUsername())) {
	 * log.warn("Registration failed - Username already exists: {}",
	 * registrationDTO.getUsername()); throw new
	 * UserAlreadyExistsException("Username already taken"); }
	 * 
	 * // Create new user User user = new User();
	 * user.setUsername(registrationDTO.getUsername());
	 * user.setEmail(registrationDTO.getEmail());
	 * user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
	 * user.setGender(registrationDTO.getGender());
	 * user.setPhoneNumber(registrationDTO.getPhoneNumber());
	 * user.setEducation(registrationDTO.getEducation());
	 * user.setAddress(registrationDTO.getAddress()); user.setEnabled(false); //
	 * User is disabled until email verification
	 * 
	 * // Handle author application if requested if
	 * (registrationDTO.isWantToBeAuthor()) {
	 * user.setBio(registrationDTO.getAuthorBio());
	 * user.setExpertise(registrationDTO.getAuthorExpertise());;
	 * user.setAuthorApprovalRequested(true);
	 * user.setAuthorApprovalStatus("PENDING"); }
	 * 
	 * // Set default role Role userRole = roleRepository.findByName("ROLE_USER")
	 * .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
	 * user.setRoles(Collections.singleton(userRole));
	 * 
	 * // Save user User savedUser = userRepository.save(user);
	 * log.info("User registered successfully with ID: {}", savedUser.getId());
	 * 
	 * // Send verification email try {
	 * emailService.sendVerificationEmail(savedUser);
	 * log.info("Verification email sent to: {}", savedUser.getEmail()); } catch
	 * (Exception e) { log.error("Failed to send verification email to: {}",
	 * savedUser.getEmail(), e); }
	 * 
	 * return savedUser; }
	 * 
	 * 
	 * @Override public User registerUserForm(UserRegistrationDTO userDTO) { //
	 * Validate input if (userDTO == null) { throw new
	 * IllegalArgumentException("User registration data cannot be null"); }
	 * 
	 * // Check uniqueness if (userRepository.existsByEmail(userDTO.getEmail())) {
	 * throw new UserAlreadyExistsException("Email already exists"); }
	 * 
	 * if (userRepository.existsByUsername(userDTO.getUsername())) { throw new
	 * UserAlreadyExistsException("Username already exists"); }
	 * 
	 * // Create and save user User user = new User();
	 * user.setUsername(userDTO.getUsername()); user.setEmail(userDTO.getEmail());
	 * user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
	 * user.setGender(userDTO.getGender());
	 * user.setPhoneNumber(userDTO.getPhoneNumber());
	 * user.setEducation(userDTO.getEducation());
	 * user.setAddress(userDTO.getAddress()); user.setEnabled(false);
	 * user.setAccountNonExpired(true); user.setAccountNonLocked(true);
	 * user.setCredentialsNonExpired(true);
	 * 
	 * // Assign roles Set<Role> userRoles = userDTO.getRoleNames().stream()
	 * .map(roleName -> { try { return roleRepository.findByName(roleName)
	 * .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
	 * } catch (RoleNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return null; }) .collect(Collectors.toSet());
	 * 
	 * user.setRoles(userRoles); user = userRepository.save(user);
	 * 
	 * // Send verification email String token =
	 * jwtUtils.generateJwtToken(user.getUsername()); // Use username instead of
	 * email
	 * 
	 * // Create Verification Link String verificationLink =
	 * "http://localhost:8080/api/user/verify-email?token=" + token;
	 * 
	 * emailService.sendVerificationEmail(user.getEmail(),
	 * "Verify your email address", // subject
	 * "Click the link to verify your email: " + verificationLink // body text );
	 * 
	 * return user; }
	 */

	@Override
	public String verifyEmail(String token) {
		if (!jwtUtils.validateJwtToken(token)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
		}

		String username = jwtUtils.getUserNameFromJwtToken(token);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Username not found"));
		user.setEnabled(true);

		// Assign roles during email verification (you can modify this logic as per your
		// needs)
		Set<Role> defaultRoles = user.getRoles();
		if (defaultRoles.isEmpty()) {
			Role authRole = roleRepository.findByName("AUTHOR")
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));
			defaultRoles.add(authRole);
		}

		user.setRoles(defaultRoles);

		userRepository.save(user);

		// Send confirmation email after assigning roles
		sendAuthorConfirmationEmail(user);

		return "Email verified successfully, you are now an author";
	}

	@Override
	public void sendVerificationEmail(User user, String token) {
		String baseUrl = System.getenv("BASE_URL") != null ? System.getenv("BASE_URL") : "http://localhost:8080";
		String confirmationUrl = baseUrl + "/api/auth/verify?token=" + token;

		String subject = "Email Verification";
		String body = "Click the link to verify your email: " + confirmationUrl;

		emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}

	@Override
	public void sendAuthorConfirmationEmail(User user) {
		String subject = "Congratulations! You are now an Author!";
		String body = "Hello " + user.getUsername() + ",\n\n"
				+ "Your email has been successfully verified, and you are now an author on our platform. "
				+ "You can now access author-related features.\n\n" + "Best regards,\nThe Team";

		emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}

	/*
	 * @Override public User registerUserForm(UserRegistrationDTO userDTO) { //
	 * Check if email already exists if
	 * (userRepository.existsByEmail(userDTO.getEmail())) { throw new
	 * RuntimeException("Email already exists"); }
	 * 
	 * // Check if the password is valid if (userDTO.getPassword() == null ||
	 * userDTO.getPassword().isEmpty()) { throw new
	 * IllegalArgumentException("Password cannot be null or empty"); }
	 * 
	 * // Map DTO to Entity User user = new User();
	 * user.setUsername(userDTO.getUsername()); user.setEmail(userDTO.getEmail());
	 * user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // ✅ Encrypt
	 * password user.setGender(userDTO.getGender());
	 * user.setPhoneNumber(userDTO.getPhoneNumber());
	 * user.setEducation(userDTO.getEducation());
	 * user.setAddress(userDTO.getAddress()); user.setEnabled(false); // ✅ User is
	 * disabled until email verification
	 * 
	 * // ✅ Assign Role Set<Role> userRoles = userDTO.getRoleNames().stream()
	 * .map(roleName -> roleRepository.findByName(roleName) .orElseThrow(() -> new
	 * RuntimeException("Role not found: " + roleName)))
	 * .collect(Collectors.toSet());
	 * 
	 * // ✅ Save User user = userRepository.save(user);
	 * 
	 * // ✅ Generate Email Verification Token String token =
	 * jwtUtils.generateJwtToken(user.getEmail()); // Use email instead of
	 * toString()
	 * 
	 * // ✅ Create Verification Link String verificationLink =
	 * "http://localhost:8080/api/auth/verify?token=" + token;
	 * 
	 * // ✅ Send Verification Email sendVerificationEmail(user, verificationLink);
	 * 
	 * return user; }
	 * 
	 * 
	 * /*
	 * 
	 * @Override public User rigisterUserForm(UserRegistrationDTO userDTO) { //
	 * Check if email already exists
	 * if(userRepository.existsByEmail(userDTO.getEmail())) { throw new
	 * RuntimeException("Email already exists"); }
	 * 
	 * // Check if the password is not null or empty if (userDTO.getPassword() ==
	 * null || userDTO.getPassword().isEmpty()) { throw new
	 * IllegalArgumentException("Password cannot be null or empty"); }
	 * 
	 * // Map UserRegistrationDTO to User entity User user = mapper.toUser(userDTO);
	 * 
	 * // Encode the password before saving to the database
	 * user.setUsername(userDTO.getUsername()); user.setEmail(userDTO.getEmail());
	 * user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
	 * user.setGender(userDTO.getGender());
	 * user.setPhoneNumber(userDTO.getPhoneNumber());
	 * user.setEducation(userDTO.getEducation());
	 * user.setAddress(userDTO.getAddress()); user.setEnabled(false); // Make sure
	 * to set the default enabled status as false
	 * 
	 * // Generate JWT token for email verification String token =
	 * jwtUtils.generateJwtToken(user.toString());
	 * 
	 * // Send the verification email sendVerificationEmail(user, token);
	 * 
	 * return userRepository.save(user); }
	 * 
	 * @Override public String verifyEmail(String token) { if
	 * (!jwtUtils.validateJwtToken(token)) { // Check for invalid token first throw
	 * new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token"); }
	 * 
	 * String username = jwtUtils.getUserNameFromJwtToken(token); User user =
	 * userRepository.findByUsername(username) .orElseThrow(() -> new
	 * ApiException(HttpStatus.BAD_REQUEST, "Username not found"));
	 * user.setEnabled(true);
	 * 
	 * // Assign default roles user Role authRole =
	 * roleRepository.findByName("AUTHOR") .orElseThrow(() -> new
	 * ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));
	 * 
	 * user.getRoles().add(authRole);
	 * 
	 * userRepository.save(user);
	 * 
	 * // Sent confirmation email to user sendAuthorConfirmationEmail(user);
	 * 
	 * String message = "Email verified successfully, you are now an author";
	 * 
	 * return message; }
	 * 
	 * @Override public void sendVerificationEmail(User user, String token) { String
	 * confirmationUrl = "http://localhost:8080/verify-email?token=" + token;
	 * 
	 * String subject = "Email Verification"; String body =
	 * "Click the link to verify your email: ";
	 * 
	 * emailService.sendVerificationEmail(user.getEmail(), subject, body +
	 * confirmationUrl); }
	 * 
	 * @Override public void sendAuthorConfirmationEmail(User user) { String subject
	 * = "Congratulations! You are now an Author!"; String body = "Hello " +
	 * user.getUsername() + ",\n\n" +
	 * "Your email has been successfully verified, and you are now an author on our platform. "
	 * + "You can now access author-related features.\n\n" +
	 * "Best regards,\nThe Team";
	 * 
	 * emailService.sendVerificationEmail(user.getEmail(), subject, body); }
	 */

}
