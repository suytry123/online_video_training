package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class SignupUser {
	private Long id;
	private String username;
	private String password;
	private String email;
}
