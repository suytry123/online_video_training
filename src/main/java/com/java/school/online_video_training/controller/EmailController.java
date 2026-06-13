package com.java.school.online_video_training.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.ApiResponse;
import com.java.school.online_video_training.dto.ResendVerificationRequest;
import com.java.school.online_video_training.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailController {
	private final EmailService emailService;

	@Value("${app.frontend-url}")
	private String frontendUrl;

	@GetMapping("/verify-email")
	public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {

		log.info("Verification token received: {}", token);

		try {

			emailService.verifyEmail(token);

			String redirectUrl = frontendUrl + "/verification-success";

			log.info("Redirecting to: {}", redirectUrl);

			response.sendRedirect(redirectUrl);

		} catch (Exception e) {

			String redirectUrl = frontendUrl + "/verification-failed";

			log.info("Redirecting to: {}", redirectUrl);

			response.sendRedirect(redirectUrl);
		}
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(@RequestBody ResendVerificationRequest request) {

		emailService.resendVerificationEmail(request.getEmail());
		
		return ResponseEntity.ok(new ApiResponse<>(true, "Verification email sent successfully.", null));
	}

	/*@GetMapping("/verify-email")
	public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {

		emailService.verifyEmail(token);

		return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully.", null));
	}*/

	

}
