package com.java.school.online_video_training.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.mapper.UserMapper;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserContorller {
	
	@Autowired
	private UserService userService;
	
	  @PostMapping("registerForm")
	    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO registrationDTO, BindingResult result) {
	        // Check if there are validation errors
	        if (result.hasErrors()) {
	            result.getAllErrors().forEach(error -> {
	                log.info("Validation error: " + error.getDefaultMessage());
	            });
	            return new ResponseEntity<>(result.getAllErrors().toString(), HttpStatus.BAD_REQUEST);
	        }

	        // Log received password and user info (for debugging)
	        log.info("Received password: " + registrationDTO.getPassword());
	        log.info("Received User: " + registrationDTO);

	        // No need to check password manually, as @NotBlank already ensures it's not null or empty
	        User user = userService.registerUserForm(registrationDTO);
	        return ResponseEntity.ok(user);
	    }

		@GetMapping("verify-email")
		public ResponseEntity<?> verifyEmail(@RequestParam String token) {
			String message = userService.verifyEmail(token);
			return ResponseEntity.ok(message);
		}

	
	/*
	@PostMapping("registerForm")
	public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
		 System.out.println("Received password: " + registrationDTO.getPassword());
		
	    System.out.println("Received User: " + registrationDTO);

	    if (registrationDTO.getPassword() == null || registrationDTO.getPassword().isEmpty()) {
	        throw new IllegalArgumentException("Password cannot be null or empty");
	    }

	    User user = userService.registerUserForm(registrationDTO);
	    return ResponseEntity.ok(user);
	}

	
	@GetMapping("verify-email")
	public ResponseEntity<?> verifyEmail(@RequestParam String token){
		String message = userService.verifyEmail(token);
		return ResponseEntity.ok(message);
	}*/
}
