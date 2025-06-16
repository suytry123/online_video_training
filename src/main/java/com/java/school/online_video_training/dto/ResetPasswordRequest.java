package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	 private String phone;
	 private String newPassword;
}
