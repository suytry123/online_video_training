package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailController {
	private final EmailService emailService;

	@GetMapping("/verify")
	public ResponseEntity<?> verifyEmail(@RequestParam String token) {

		boolean verified = emailService.verifyEmail(token);

		if (!verified) {

			return ResponseEntity.badRequest().body("Invalid or expired token");
		}

		return ResponseEntity.ok("Email verified successfully");
	}

}
