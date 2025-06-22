package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.OtpRequestDTO;
import com.java.school.online_video_training.dto.OtpVerifyDTO;
import com.java.school.online_video_training.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequestDTO request) {
        otpService.sendOtp(request.getUsernameOrEmailOrPhone());
        return ResponseEntity.ok("OTP sent if user exists.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDTO request) {
        boolean success = otpService.verifyOtp(request.getOtp(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }
    
}
