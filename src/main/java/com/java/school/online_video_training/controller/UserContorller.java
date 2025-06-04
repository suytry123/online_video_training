package com.java.school.online_video_training.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserContorller {

	private final UserService userService;
	private final LocalValidatorFactoryBean validator;
	private final ObjectMapper objectMapper;

	@PostMapping("/applyForAuthor")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> applyForAuthor(@RequestBody String rawBody) {
		try {
			log.info("Raw request body for author application: {}", rawBody);

			// Parse JSON to DTO
			UserRegistrationDTO registrationDTO = objectMapper.readValue(rawBody, UserRegistrationDTO.class);
			log.info("Parsed DTO for author application: {}", registrationDTO);

			// Validate the DTO
			Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(registrationDTO);
			if (!violations.isEmpty()) {
				log.error("Validation errors: {}", violations);
				Map<String, String> errors = new HashMap<>();
				violations.forEach(
						violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
				return ResponseEntity.badRequest().body(errors);
			}

			// Process author application
			User user = userService.applyForAuthor(registrationDTO);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			log.error("Author application error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}
	}

	@GetMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@RequestParam String token) {
		String message = userService.verifyEmail(token);
		return ResponseEntity.ok(message);
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		String html = """
					<html><body><h2>Test endpoint works!</h2></body></html>
				""";
		return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
	}

	@GetMapping("/author/approve")
	public ResponseEntity<String> approveAuthor(@RequestParam String token) {
		try {
			String result = userService.handleAuthorApproval(token);
			String htmlResponse = String.format("""
					<html>
					  <head><title>Author Approval</title></head>
					  <body style='font-family:sans-serif;text-align:center;margin-top:50px'>
					    <h2 style='color:green;'>✅ %s</h2>
					    <p>You may now close this window.</p>
					  </body>
					</html>
					""", result);
			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse);
		} catch (Exception e) {
			log.error("Error in approveAuthor for token {}: ", token, e); // Add this line
			String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
			String errorHtml = String.format("""
					<html>
					  <head><title>Approval Error</title></head>
					  <body style='font-family:sans-serif;text-align:center;margin-top:50px'>
					    <h2 style='color:red;'>❌ Error: %s</h2>
					    <p>Please try again later.</p>
					  </body>
					</html>
					""", errorMsg);
			return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(errorHtml);
		}
	}

	@GetMapping("/author/reject")
	public ResponseEntity<?> rejectAuthor(@RequestParam String token) {
		try {
			String result = userService.handleAuthorRejection(token);
			String htmlResponse = String.format("""
					<html>
					  <head><title>Author Rejection</title></head>
					  <body style='font-family:sans-serif;text-align:center;margin-top:50px'>
						<h2 style='color:orange;'>⚠️ %s</h2>
						<p>You may now close this window.</p>
					  </body>
					</html>
					""", result);
			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse);
		} catch (Exception e) {
			String errorHtml = String.format("""
					<html>
					  <head><title>Rejection Error</title></head>
					  <body style='font-family:sans-serif;text-align:center;margin-top:50px'>
						<h2 style='color:red;'>❌ Error: %s</h2>
						<p>Please try again later.</p>
					  </body>
					</html>
					""", e.getMessage());
			return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(errorHtml);
		}
	}

	@PostMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> uploadPhoto(@PathVariable Long userId, @RequestPart("photo") MultipartFile photo) {
		try {
			User user = userService.uploadPhoto(userId, photo);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo upload failed");
		}
	}

	@GetMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:read')")
	public ResponseEntity<?> getPhoto(@PathVariable Long userId) {
		try {
			User user = userService.getPhotoById(userId);
			String photo = user.getPhoto();
			if (photo == null) {
				return ResponseEntity.notFound().build();
			}
			Path filePath = Paths.get("src/main/resources/file-repository/", photo);
			if (!Files.exists(filePath)) {
				return ResponseEntity.notFound().build();
			}
			String contentType = Files.probeContentType(filePath);
			byte[] fileBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo retrieval failed");
		}
	}

	@GetMapping("/photos")
	@PreAuthorize("hasAuthority('user:read')")
	public ResponseEntity<?> getPhotos(@RequestParam Map<String, String> photos) {
		try {
			Page<Map<String, String>> photoMetadata = userService.getPhotoMetadata(photos);
			PageDTO dto = new PageDTO(photoMetadata);
			log.info("Photo metadata retrieved successfully");
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			log.error("Failed to get photo metadata", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve photo metadata: " + e.getMessage());
		}
	}

	@PutMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> updatePhoto(@PathVariable Long userId, @RequestPart("photo") MultipartFile photo) {
		try {
			User user = userService.updatePhoto(userId, photo);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo update failed");
		}
	}

	@DeleteMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> deletePhoto(@PathVariable Long userId) {
		userService.deletePhoto(userId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup_user")
	public ResponseEntity<?> createUserAcc(@Valid @RequestBody SignupUser signupUser) {
		String jwt = userService.signupUser(signupUser);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeaders).build();
	}
}