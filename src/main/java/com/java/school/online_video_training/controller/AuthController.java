package com.java.school.online_video_training.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.config.jwt.LoginRequest;
import com.java.school.online_video_training.config.jwt.LoginResponse;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.ForgotPasswordRequest;
import com.java.school.online_video_training.dto.MessageResponse;
import com.java.school.online_video_training.dto.ResetPasswordRequest;
import com.java.school.online_video_training.dto.SignupRequest;
import com.java.school.online_video_training.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	/*
	 * @PostMapping("/signin") public ResponseEntity<?>
	 * authenticateUser(@Valid @RequestBody LoginRequest loginRequest) { String jwt
	 * = authService.authenticateUser(loginRequest);
	 * 
	 * HttpHeaders responseHeaders = new HttpHeaders();
	 * responseHeaders.set("Authorization", "Bearer " + jwt);
	 * responseHeaders.set("Access-Control-Expose-Headers", "Authorization");
	 * 
	 * return ResponseEntity.ok().headers(responseHeaders).build(); }
	 */

	@PostMapping("/signin")
	public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = authService.authenticateUser(loginRequest);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Authorization", "Bearer " + loginResponse.getToken());
		responseHeaders.set("Access-Control-Expose-Headers", "Authorization");

		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		String jwt = authService.createUser(signUpRequest);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {

		authService.forgotPassword(request.getEmail());

		return ResponseEntity.ok(new MessageResponse("If the email exists, a password reset link has been sent."));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

		authService.resetPassword(request.getToken(), request.getPassword());

		return ResponseEntity.ok(new MessageResponse("Password reset successful"));
	}
}
