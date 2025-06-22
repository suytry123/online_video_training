package com.java.school.online_video_training.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.OtpToken;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.repository.OtpTokenRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService; // Or SmsService if you want SMS
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendOtp(String usernameOrEmailOrPhone) {
        // Find user by email or phone (simplified for demo)
        Optional<User> userOpt = userRepository.findByEmail(usernameOrEmailOrPhone);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByPhoneNumber(usernameOrEmailOrPhone);
        }
        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP
        OtpToken otpToken = new OtpToken();
        otpToken.setOtp(otp);
        otpToken.setUser(user);
        otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        otpTokenRepository.save(otpToken);

        // Send OTP via email or SMS
        emailService.sendOtp(user.getEmail(), otp); // Or use SMS service
    }

    @Override
    public boolean verifyOtp(String otp, String newPassword) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository.findByOtp(otp);
        if (otpTokenOpt.isEmpty()) return false;

        OtpToken otpToken = otpTokenOpt.get();
        if (otpToken.isUsed() || otpToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        User user = otpToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        return true;
    }
}
