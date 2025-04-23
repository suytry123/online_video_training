package com.java.school.online_video_training.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.relation.RoleNotFoundException;

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

import lombok.RequiredArgsConstructor;

@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper mapper;
	private final JwtUtils jwtUtils;

	@Override
	public Optional<AuthUser> findUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new ResourceNotFoundException("User", username));
		
		AuthUser authUser = AuthUser.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(getAuthorities(user.getRoles()))
				.accountNonExpired(user.isAccountNonExpired())
				.accountNonLocked(user.isAccountNonLocked())
				.credentialsNonExpired(user.isCredentialsNonExpired())
				.enabled(user.isEnabled())
				.build();
		return Optional.ofNullable(authUser);
	}
	public Set<SimpleGrantedAuthority> getAuthorities(Set<Role> roles){
		Set<SimpleGrantedAuthority> authorities1 = roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
			.collect(Collectors.toSet());
		 Set<SimpleGrantedAuthority> authorities = roles.stream()
			.flatMap(role -> toStream(role))
			.collect(Collectors.toSet());
		 authorities.addAll(authorities1);
		 return authorities;
	}
	
	private Stream<SimpleGrantedAuthority> toStream(Role role){
		return role.getPermissions().stream()
			.map(permiision -> new SimpleGrantedAuthority(permiision.getName()));
	}
	
	@Override
	public User registerUserForm(UserRegistrationDTO userDTO) {
//	    // Check if email already exists
//	    if (userRepository.existsByEmail(userDTO.getEmail())) {
//	        throw new RuntimeException("Email already exists");
//	    }
//
//	    // Check if username already exists
//	    if (userRepository.existsByUsername(userDTO.getUsername())) {
//	        throw new RuntimeException("Username already exists");
//	    }
		
		if (userRepository.existsByEmail(userDTO.getEmail())) {
		    throw new UserAlreadyExistsException("Email already exists");
		}

		if (userRepository.existsByUsername(userDTO.getUsername())) {
		    throw new UserAlreadyExistsException("Username already exists");
		}

	    // Check if the password is valid
	    if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
	        throw new IllegalArgumentException("Password cannot be null or empty");
	    }

	    // Map DTO to Entity
	   
	    //User user = mapper.toUser(userDTO);
	    User user = new User();
	    user.setUsername(userDTO.getUsername());
	    user.setEmail(userDTO.getEmail());
	    user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // ✅ Encrypt password
	    user.setGender(userDTO.getGender());
	    user.setPhoneNumber(userDTO.getPhoneNumber());
	    user.setEducation(userDTO.getEducation());
	    user.setAddress(userDTO.getAddress());
	    user.setEnabled(false); // User is disabled until email verification
	    user.setAccountNonExpired(true); // Account is not expired
	    user.setAccountNonLocked(true); // Account is not locked
	    user.setCredentialsNonExpired(true);

	    // Assign roles based on the userDTO role names (may include multiple roles)
	    Set<Role> userRoles = userDTO.getRoleNames().stream()
	            .map(roleName -> {
	                try {
	                    return roleRepository.findByName(roleName)
	                            .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
	                } catch (RoleNotFoundException e) {
	                    // Log the error or handle it appropriately
	                    e.printStackTrace();
	                    // Optionally, you can throw a different exception or handle it in a way that suits your application
	                    throw new RuntimeException(e.getMessage());
	                }
	            })
	            .collect(Collectors.toSet());

	    user.setRoles(userRoles);

	    // Save User
	    user = userRepository.save(user);

	    // Generate Email Verification Token
	    String token = jwtUtils.generateJwtToken(user.getEmail()); // Secure token generation

	    // Create Verification Link
	    String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;

	    // Send Verification Email
	    //sendVerificationEmail(user, verificationLink);
	    emailService.sendVerificationEmail(
	    	    user.getEmail(),
	    	    "Verify your email address", // subject
	    	    "Click the link to verify your email: " + verificationLink // body text
	    	);

	    return user;
	}

	@Override
	public String verifyEmail(String token) {
	    if (!jwtUtils.validateJwtToken(token)) {
	        throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
	    }

	    String username = jwtUtils.getUserNameFromJwtToken(token);
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Username not found"));
	    user.setEnabled(true);

	    // Assign roles during email verification (you can modify this logic as per your needs)
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
	    String body = "Hello " + user.getUsername() + ",\n\n" +
	                  "Your email has been successfully verified, and you are now an author on our platform. " +
	                  "You can now access author-related features.\n\n" +
	                  "Best regards,\nThe Team";

	    emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}

	/*
	@Override
	public User registerUserForm(UserRegistrationDTO userDTO) {
	    // Check if email already exists
	    if (userRepository.existsByEmail(userDTO.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    // Check if the password is valid
	    if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
	        throw new IllegalArgumentException("Password cannot be null or empty");
	    }

	    // Map DTO to Entity
	    User user = new User();
	    user.setUsername(userDTO.getUsername());
	    user.setEmail(userDTO.getEmail());
	    user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // ✅ Encrypt password
	    user.setGender(userDTO.getGender());
	    user.setPhoneNumber(userDTO.getPhoneNumber());
	    user.setEducation(userDTO.getEducation());
	    user.setAddress(userDTO.getAddress());
	    user.setEnabled(false); // ✅ User is disabled until email verification

	    // ✅ Assign Role
	    Set<Role> userRoles = userDTO.getRoleNames().stream()
	            .map(roleName -> roleRepository.findByName(roleName)
	                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
	            .collect(Collectors.toSet());

	    // ✅ Save User
	    user = userRepository.save(user);

	    // ✅ Generate Email Verification Token
	    String token = jwtUtils.generateJwtToken(user.getEmail()); // Use email instead of toString()

	    // ✅ Create Verification Link
	    String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;

	    // ✅ Send Verification Email
	    sendVerificationEmail(user, verificationLink);

	    return user;
	}

	
	/*
	@Override
	public User rigisterUserForm(UserRegistrationDTO userDTO) {
	    // Check if email already exists
	    if(userRepository.existsByEmail(userDTO.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    // Check if the password is not null or empty
	    if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
	        throw new IllegalArgumentException("Password cannot be null or empty");
	    }

	    // Map UserRegistrationDTO to User entity
	    User user = mapper.toUser(userDTO);
	    
	    // Encode the password before saving to the database
	    user.setUsername(userDTO.getUsername());
	    user.setEmail(userDTO.getEmail());
	    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
	    user.setGender(userDTO.getGender());
	    user.setPhoneNumber(userDTO.getPhoneNumber());
	    user.setEducation(userDTO.getEducation());
	    user.setAddress(userDTO.getAddress());
	    user.setEnabled(false);  // Make sure to set the default enabled status as false

	    // Generate JWT token for email verification
	    String token = jwtUtils.generateJwtToken(user.toString());

	    // Send the verification email
	    sendVerificationEmail(user, token);
	    
	    return userRepository.save(user);
	}
	
	@Override
	public String verifyEmail(String token) {
		if (!jwtUtils.validateJwtToken(token)) { // Check for invalid token first
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
		}

		String username = jwtUtils.getUserNameFromJwtToken(token);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Username not found"));
		user.setEnabled(true);

		// Assign default roles user
		Role authRole = roleRepository.findByName("AUTHOR")
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role Author not found"));

		user.getRoles().add(authRole);

		userRepository.save(user);

		// Sent confirmation email to user
		sendAuthorConfirmationEmail(user);

		String message = "Email verified successfully, you are now an author";

		return message;
	}
	
	@Override
	public void sendVerificationEmail(User user, String token) {
		String confirmationUrl = "http://localhost:8080/verify-email?token=" + token;
		
		String subject = "Email Verification";
		String body = "Click the link to verify your email: ";
		
		emailService.sendVerificationEmail(user.getEmail(), subject,
				body + confirmationUrl);
	}
	@Override
	public void sendAuthorConfirmationEmail(User user) {
		String subject = "Congratulations! You are now an Author!";
        String body = "Hello " + user.getUsername() + ",\n\n" +
                      "Your email has been successfully verified, and you are now an author on our platform. " +
                      "You can now access author-related features.\n\n" +
                      "Best regards,\nThe Team";
        
        emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}*/

}
