package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class OtpVerifyDTO {
    private String otp;
    private String newPassword;
}
