package com.java.school.online_video_training.config.jwt;

import java.util.List;

import lombok.Data;

@Data
public class LoginResponse {

	private final String token;
	private final String username;	
	private final String email;
	private final List<String> roles;

}
