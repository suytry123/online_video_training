package com.java.school.online_video_training.dto;

import java.time.LocalDateTime;

import com.java.school.online_video_training.config.security.AuthorApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorApplicationResponseDTO {

	private Long applicationId;

	private Long userId;

	private String username;

	private AuthorApprovalStatus status;

	private LocalDateTime submittedAt;

	private String education;

	private String address;

	private String authorBio;

	private String authorExpertise;
	
	private String message;
}