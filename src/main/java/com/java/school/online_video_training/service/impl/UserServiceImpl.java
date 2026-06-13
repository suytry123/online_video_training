package com.java.school.online_video_training.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.config.security.AuthorApprovalStatus;
import com.java.school.online_video_training.config.security.JwtUtils;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.AuthorApplicationDTO;
import com.java.school.online_video_training.dto.AuthorApplicationResponseDTO;
import com.java.school.online_video_training.dto.CategoryDTO;
import com.java.school.online_video_training.dto.MessageResponse;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserPhotoDTO;
import com.java.school.online_video_training.dto.UserProfileDTO;
import com.java.school.online_video_training.dto.UserProfileUpdateDTO;
import com.java.school.online_video_training.entity.AuthorApplication;
import com.java.school.online_video_training.entity.Category;
import com.java.school.online_video_training.entity.Role;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.mapper.UserMapper;
import com.java.school.online_video_training.repository.AuthorApplicationRepository;
import com.java.school.online_video_training.repository.RoleRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.util.PageUtil;
import com.java.school.online_video_training.spec.AuthorApplicationFilter;
import com.java.school.online_video_training.spec.AuthorApplicationSpec;
import com.java.school.online_video_training.spec.CategoryFilter;
import com.java.school.online_video_training.spec.CategorySpec;
import com.java.school.online_video_training.spec.UserFilter;
import com.java.school.online_video_training.spec.UserSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final AuthorApplicationRepository authorApplicationRepository;
	private final RoleRepository roleRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper mapper;
	private final JwtUtils jwtUtils;
	
	@Value("${app.admin-email}")
	private String adminEmail;
	
	@Value("${app.base-url}")
	private String baseUrl;
	
	public static final String AUTHOR = "AUTHOR";

	@Override
	public Optional<AuthUser> findUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", username));

		AuthUser authUser = AuthUser.builder().id(user.getId()).username(user.getUsername()).password(user.getPassword())
				.authorities(getAuthorities(user.getRoles())).accountNonExpired(user.isAccountNonExpired())
				.accountNonLocked(user.isAccountNonLocked()).credentialsNonExpired(user.isCredentialsNonExpired())
				.enabled(user.isEnabled()).build();
		return Optional.ofNullable(authUser);
	}
	
	@Override
	public AuthUser findUserByEmail(String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "User with email = " + email + " not found"));

	    return AuthUser.builder()
	            .id(user.getId())
	            .username(user.getUsername())
	            .email(user.getEmail())
	            .password(user.getPassword())
	            .roles(user.getRoles())
	            .authorities(getAuthorities(user.getRoles()))
	            .accountNonExpired(user.isAccountNonExpired())
	            .accountNonLocked(user.isAccountNonLocked())
	            .credentialsNonExpired(user.isCredentialsNonExpired())
	            .enabled(user.isEnabled())
	            .build();
	}

	public Set<SimpleGrantedAuthority> getAuthorities(Set<Role> roles) {
		Set<SimpleGrantedAuthority> authorities1 = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toSet());
		Set<SimpleGrantedAuthority> authorities = roles.stream().flatMap(role -> toStream(role))
				.collect(Collectors.toSet());
		authorities.addAll(authorities1);
		return authorities;
	}

	private Stream<SimpleGrantedAuthority> toStream(Role role) {
		return role.getPermissions().stream().map(permiision -> new SimpleGrantedAuthority(permiision.getName()));
	}
	
	@Transactional
	@Override
	public MessageResponse signupUser(SignupUser signupUser) {
		if (userRepository.existsByUsername(signupUser.getUsername())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken!");
		}

		if (userRepository.existsByEmail(signupUser.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already exists!");
		}

		// Create new user's account
		User user = new User(signupUser.getId(), signupUser.getUsername(), signupUser.getEmail(),
		        passwordEncoder.encode(signupUser.getPassword()));

//		user.setRoles(Role.USER);
		// Fetch existing role "USER" from DB
	    Role userRole = roleRepository.findByName("USER")
	        .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "USER role not found"));
	    
//	    String token = UUID.randomUUID().toString();
	    String token = generateVerificationToken();

	    user.setEnabled(false);

	    user.setVerificationToken(token);

	    user.setVerificationTokenExpiry(
	            LocalDateTime.now().plusHours(24));

	    // Assign role to user
	    user.setRoles(Collections.singleton(userRole));
		userRepository.save(user);
		emailService.sendUserVerificationEmail(user);

		return new MessageResponse(
			    "Registration successful. Please verify your email."
			);
		//		return jwtUtils.generateJwtToken(signupUser.getUsername());
	}
	
	private String generateVerificationToken() {
		SecureRandom random = new SecureRandom();

		byte[] bytes = new byte[32];

		random.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
	
	@Override
	public UserProfileDTO getProfile(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		UserProfileDTO dto = new UserProfileDTO();

		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setGender(user.getGender());
		dto.setPhoto(user.getPhoto());
		dto.setJoinDate(user.getJoinDate());

		return dto;
	}

	@Override
	public UserProfileDTO updateProfile(Long userId, UserProfileUpdateDTO dto) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		user.setPhoneNumber(dto.getPhoneNumber());
		user.setGender(dto.getGender());

		userRepository.save(user);

		return getProfile(userId);
	}
	
	@Override
	@Transactional
	public AuthorApplicationResponseDTO submitAuthorApplication(AuthorApplicationDTO dto) {

		User user = getCurrentUser();

		if (hasRole(user, AUTHOR)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "User is already an author");
		}

		Optional<AuthorApplication> latestApplication = authorApplicationRepository
				.findTopByApplicantOrderByRequestedAtDesc(user);

		if (latestApplication.isPresent()) {

			AuthorApprovalStatus status = latestApplication.get().getStatus();

			if (status == AuthorApprovalStatus.PENDING) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "You already have a pending application");
			}

			if (status == AuthorApprovalStatus.APPROVED) {

				throw new ApiException(HttpStatus.BAD_REQUEST, "You are already an approved author");
			}

		}

		AuthorApplication application = new AuthorApplication();

		application.setApplicant(user);

		application.setEducation(dto.getEducation());
		application.setAddress(dto.getAddress());
		application.setBio(dto.getAuthorBio());
		application.setExpertise(dto.getAuthorExpertise());

//		application.setActionToken(UUID.randomUUID().toString());

		LocalDateTime now = LocalDateTime.now();

		application.setRequestedAt(now);

		application.setStatus(AuthorApprovalStatus.PENDING);

		application = authorApplicationRepository.save(application);

		emailService.sendAuthorApprovalRequestEmail(application);

		return AuthorApplicationResponseDTO.builder().applicationId(application.getId()).userId(user.getId())
				.username(user.getUsername()).status(application.getStatus()).submittedAt(application.getRequestedAt())
				.message("Author application submitted successfully").build();
	}

	private void validateApplicationStatus(AuthorApplication application) {

		if (application.getStatus() != AuthorApprovalStatus.PENDING) {

			throw new ApiException(HttpStatus.BAD_REQUEST, "Application already processed");
		}

	}

	@Override
	@Transactional
	public String approveAuthorApplication(Long applicationId) {

		AuthorApplication application = authorApplicationRepository.findById(applicationId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Application not found"));

		validateApplicationStatus(application);

		User applicant = application.getApplicant();

		Role authorRole = roleRepository.findByName(AUTHOR)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Author role not found"));

		if (!hasRole(applicant, AUTHOR)) {
			applicant.getRoles().add(authorRole);
		}

		User admin = getCurrentUser();

		log.info("Author approved. applicantId={}, adminId={}", applicant.getId(), admin.getId());

		application.setStatus(AuthorApprovalStatus.APPROVED);

		LocalDateTime now = LocalDateTime.now();

		application.setApprovedAt(now);

		application.setApprovedBy(admin);

		userRepository.save(applicant);

		authorApplicationRepository.save(application);

		emailService.sendAuthorApprovalStatusEmail(application, true);

		emailService.sendAdminActionConfirmation(application, true);

		return "Author application approved successfully";
	}

	@Override
	@Transactional
	public String rejectAuthorApplication(Long applicationId) {

		AuthorApplication application = authorApplicationRepository
			    .findById(applicationId)
			    .orElseThrow(() -> new ApiException(
			        HttpStatus.NOT_FOUND,
			        "Application not found"));

		validateApplicationStatus(application);

		User admin = getCurrentUser();

		log.info("Author rejected. applicantId={}, adminId={}", application.getApplicant().getId(), admin.getId());

		application.setStatus(AuthorApprovalStatus.REJECTED);

		LocalDateTime now = LocalDateTime.now();

		application.setRejectedAt(now);

		application.setRejectedBy(admin);

		authorApplicationRepository.save(application);

		emailService.sendAuthorApprovalStatusEmail(application, false);

		emailService.sendAdminActionConfirmation(application, false);

		return "Author application rejected successfully";
	}
	
	@Override
	public Page<AuthorApplicationResponseDTO> getAuthorApplications(Map<String, String> params) {
		AuthorApplicationFilter authorApplicationFilter = new AuthorApplicationFilter();
		if (params.containsKey("status")) {

			authorApplicationFilter.setStatus(AuthorApprovalStatus.valueOf(params.get("status").toUpperCase()));
		}
		if (params.containsKey("username")) {
			String username = params.get("username");
			authorApplicationFilter.setUsername(username);

		}
		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (params.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
		}
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (params.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
		}
		
		AuthorApplicationSpec authorApplicationSpec = new AuthorApplicationSpec(authorApplicationFilter);
		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		Page<AuthorApplication> page = authorApplicationRepository.findAll(authorApplicationSpec, pageable);
		return page.map(application -> {

			User applicant = application.getApplicant();

			return AuthorApplicationResponseDTO.builder().applicationId(application.getId()).userId(applicant.getId())
					.username(applicant.getUsername()).status(application.getStatus())
					.submittedAt(application.getRequestedAt()).education(application.getEducation())
					.address(application.getAddress()).authorBio(application.getBio())
					.authorExpertise(application.getExpertise()).build();
		});
	}

	@Override
	@Transactional
	public String verifyEmail(String token) {

		if (!jwtUtils.validateJwtToken(token)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired verification token");
		}

		String email = jwtUtils.getUserNameFromJwtToken(token);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

		if (user.isEnabled()) {
			return "Email already verified.";
		}

		if (!token.equals(user.getVerificationToken())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Verification token does not match");
		}

		LocalDateTime expiry = user.getVerificationTokenExpiry();

		if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Verification token has expired");
		}

		user.setEnabled(true);
		user.setVerificationToken(null);
		user.setVerificationTokenExpiry(null);

		userRepository.save(user);

		return "Email verified successfully. You can now log in.";
	}

	@Transactional(readOnly = true)
	private User getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {

			throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}

		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private boolean hasRole(User user, String roleName) {

		return user.getRoles().stream().anyMatch(role -> roleName.equals(role.getName()));
	}

	/*
	 * @Override public void sendAuthorConfirmationEmail(User user) { String subject
	 * = "ALERT: Author Application Approved"; String body =
	 * String.format("ALERT: Author Application Approved\n\n" + "Dear %s,\n\n" +
	 * "Congratulations! Your author application has been approved.\n" +
	 * "You are now an author on our platform and can access author-related features.\n\n"
	 * + "Your author details:\n" + "-------------------\n" + "Bio: %s\n" +
	 * "Expertise: %s\n\n" +
	 * "Note: This is an automated message. Please do not reply.\n" +
	 * "Best regards,\n" + "Your Application Team", user.getUsername(),
	 * user.getBio(), user.getExpertise());
	 * 
	 * emailService.sendVerificationEmail(user.getEmail(), subject, body); }
	 */

	/*@Override
	public void sendVerificationEmail(User user, String token) {
		// String baseUrl = System.getenv("BASE_URL") != null ?
		// System.getenv("BASE_URL") : "http://localhost:8080";
		String confirmationUrl = baseUrl + "/api/auth/verify?token=" + token;

		String subject = "ALERT: Email Verification Required";
		String body = String.format("ALERT: Email Verification Required\n\n" + "Dear %s,\n\n"
				+ "Please verify your email address by clicking the link below:\n\n" + "%s\n\n"
				+ "This link will expire in 24 hours.\n\n"
				+ "Note: This is an automated message. Please do not reply.\n" + "Best regards,\n"
				+ "Your Application Team", user.getUsername(), confirmationUrl);

		emailService.sendVerificationEmail(user.getEmail(), subject, body);
	}*/

	
	@Override
	public UserPhotoDTO uploadPhoto(Long userId, MultipartFile photo) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {

			throw new IllegalStateException("Photo already exists. Use PUT to update.");
		}

		String uploadDir = "uploads/users/";

		try {

			Files.createDirectories(Paths.get(uploadDir));

			String ext = photo.getOriginalFilename() != null && photo.getOriginalFilename().contains(".")
					? photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'))
					: "";

			String fileName = UUID.randomUUID() + ext;

			Path filePath = Paths.get(uploadDir, fileName);

			Files.write(filePath, photo.getBytes());

			user.setPhoto(fileName);

			userRepository.save(user);

			return mapper.toPhotoDTO(user);

		} catch (Exception e) {

			throw new RuntimeException("Photo upload failed", e);
		}
	}

	@Override
	public UserPhotoDTO updatePhoto(Long userId, MultipartFile photo) {

		try {

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User", userId));

			String oldPhoto = user.getPhoto();

			if (oldPhoto != null) {

				Path oldFilePath = Paths.get("uploads/users/", oldPhoto);

				Files.deleteIfExists(oldFilePath);
			}

			String uploadDir = "uploads/users/";

			Files.createDirectories(Paths.get(uploadDir));

			String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();

			Path filePath = Paths.get(uploadDir, fileName);

			Files.write(filePath, photo.getBytes());

			user.setPhoto(fileName);

			userRepository.save(user);

			return mapper.toPhotoDTO(user);

		} catch (Exception e) {

			throw new RuntimeException("Photo update failed", e);
		}
	}

	@Override
	public UserPhotoDTO getPhotoById(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		return mapper.toPhotoDTO(user);
	}
	
	@Override
	public byte[] getPhotoContent(Long userId) throws IOException {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

	    if (user.getPhoto() == null || user.getPhoto().isBlank()) {
	        throw new RuntimeException("User has no photo");
	    }
	    
	    Path filePath = Paths.get("uploads/users/", user.getPhoto());
	    

	    if (!Files.exists(filePath)) {
	        return null;
	    }

	    return Files.readAllBytes(filePath);
	}

	@Override
	public Page<Map<String, String>> getPhotoMetadata(Map<String, String> photos) {

		UserFilter imageFilter = new UserFilter();

		if (photos.containsKey("photo")) {

			String name = photos.get("photo");

			imageFilter.setPhoto(name);
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;

		if (photos.containsKey(PageUtil.PAGE_LIMIT)) {

			pageLimit = Integer.parseInt(photos.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;

		if (photos.containsKey(PageUtil.PAGE_NUMBER)) {

			pageNumber = Integer.parseInt(photos.get(PageUtil.PAGE_NUMBER));
		}

		UserSpec spec = new UserSpec(imageFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<User> page = userRepository.findAll(spec, pageable);

		return page.map(user -> {

			Map<String, String> dto = new HashMap<>();

			dto.put("userId", String.valueOf(user.getId()));

			dto.put("filename", user.getPhoto());

			dto.put("url", "/api/user/" + user.getId() + "/photo");

			return dto;
		});
	}

	@Override
	public void deletePhoto(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

		String oldPhoto = user.getPhoto();

		if (oldPhoto != null) {

			Path oldFilePath = Paths.get("uploads/users/", oldPhoto);

			try {

				Files.deleteIfExists(oldFilePath);

			} catch (Exception ignored) {
			}
		}

		user.setPhoto(null);

		userRepository.save(user);
	}

	/*
	@Override
	public Page<String> getPhoto(Map<String, String> photos) {
		 UserFilter imageFilter = new UserFilter();

		if (photos.containsKey("photo")) {
			String name = photos.get("photo");
			imageFilter.setPhoto(name);
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (photos.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(photos.get(PageUtil.PAGE_LIMIT));
		}

		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (photos.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(photos.get(PageUtil.PAGE_NUMBER));
		}

		UserSpec spec = new UserSpec(imageFilter);

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);

		Page<User> page = userRepository.findAll(spec, pageable);
		Page<String> imagePaths = page.map(video -> video.getPhoto());

		return imagePaths;
	}*/
	
}
