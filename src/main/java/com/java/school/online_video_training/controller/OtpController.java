package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.OtpEmailRequestDTO;
import com.java.school.online_video_training.dto.OtpPhoneRequestDTO;
import com.java.school.online_video_training.dto.OtpPhoneVerifyDTO;
import com.java.school.online_video_training.dto.OtpVerifyEmailDTO;
import com.java.school.online_video_training.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestBody OtpEmailRequestDTO request) {
        otpService.sendOtpForEmail(request.getEmail());
        return ResponseEntity.ok("OTP sent if user exists.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyEmailDTO request) {
        boolean success = otpService.verifyOtpForEmail(request.getOtp(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }
    
    @PostMapping("/request-phone")
    public ResponseEntity<?> requestOtpForPhone(@RequestBody OtpPhoneRequestDTO request) {
        otpService.sendOtpForPhone(request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent to phone if user exists.");
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyOtpForPhone(@RequestBody OtpPhoneVerifyDTO request) {
        boolean success = otpService.verifyOtpForPhone(request.getPhoneNumber(), request.getOtp(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }
    
}
