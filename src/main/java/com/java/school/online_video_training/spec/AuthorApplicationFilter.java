package com.java.school.online_video_training.spec;

import com.java.school.online_video_training.config.security.AuthorApprovalStatus;

import lombok.Data;

@Data
public class AuthorApplicationFilter {
	private String username;

	private AuthorApprovalStatus status;
}
