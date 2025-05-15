package com.java.school.online_video_training.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserContorller {

	private final UserService userService;
	private final LocalValidatorFactoryBean validator;
	private final ObjectMapper objectMapper;

	public UserContorller(UserService userService, LocalValidatorFactoryBean validator, ObjectMapper objectMapper) {
		this.userService = userService;
		this.validator = validator;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/applyForAuthor")
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
	
/*
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
			String errorHtml = String.format("""
			    <html>
			      <head><title>Approval Error</title></head>
			      <body style='font-family:sans-serif;text-align:center;margin-top:50px'>
			        <h2 style='color:red;'>❌ Error: %s</h2>
			        <p>Please try again later.</p>
			      </body>
			    </html>
			    """, e.getMessage());
			return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(errorHtml);
		}
	}

	@GetMapping("/author/reject")
	public ResponseEntity<String> rejectAuthor(@RequestParam String token) {
		try {
			String result = userService.handleAuthorRejection(token); // Use correct method if different
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
	}*/
	
	@GetMapping("/author/approve")
	public ResponseEntity<String> approveAuthor(@RequestParam String token) {
		try {
			String result = userService.handleAuthorApproval(token);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/author/reject")
	public ResponseEntity<String> rejectAuthor(@RequestParam String token) {
		try {
			String result = userService.handleAuthorApproval(token);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/signup_user")
	public ResponseEntity<?> createUserAcc(@Valid @RequestBody SignupUser signupUser) {
		String jwt = userService.signupUser(signupUser);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeaders).build();
	}
}