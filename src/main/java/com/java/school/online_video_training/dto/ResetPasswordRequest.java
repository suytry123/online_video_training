package com.java.school.online_video_training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

	@NotBlank
	private String token;

	@NotBlank
	@Size(min = 8, max = 100)
	private String password;
}