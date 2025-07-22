package com.java.school.online_video_training.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.OtpToken;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.repository.OtpTokenRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;
    @Value("${twilio.auth-token}")
    private String twilioAuthToken;
    @Value("${twilio.from-number}")
    private String twilioFromNumber;

    @Override
    public void sendOtpForEmail(String usernameOrEmailOrPhone) {
        Optional<User> userOpt = userRepository.findByEmail(usernameOrEmailOrPhone);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByPhoneNumber(usernameOrEmailOrPhone);
        }
        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpToken otpToken = new OtpToken();
        otpToken.setOtp(otp);
        otpToken.setUser(user);
        otpToken.setExpireDate(LocalDateTime.now().plusMinutes(10));
        otpTokenRepository.save(otpToken);
        emailService.sendOtp(user.getEmail(), otp);
    }

    @Override
    public boolean verifyOtpForEmail(String otp, String newPassword) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository.findByOtp(otp);
        if (otpTokenOpt.isEmpty()) return false;
        OtpToken otpToken = otpTokenOpt.get();
        if (otpToken.isUsed() || otpToken.getExpireDate().isBefore(LocalDateTime.now())) return false;
        User user = otpToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
        return true;
    }

    @Override
    public void sendOtpForPhone(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpToken otpToken = new OtpToken();
        otpToken.setOtp(otp);
        otpToken.setUser(user);
        otpToken.setExpireDate(LocalDateTime.now().plusMinutes(10));
        otpTokenRepository.save(otpToken);
        // Send OTP via Twilio SMS (best practice: handle exceptions)
        try {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(twilioFromNumber),
                "Your OTP code is: " + otp
            ).create();
        } catch (Exception e) {
            // Log error (best practice: use a logger, not System.out)
            // logger.error("Failed to send OTP SMS", e);
        }
    }

    @Override
    public boolean verifyOtpForPhone(String phoneNumber, String otp, String newPassword) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOpt.isPresent()) return false;
        User user = userOpt.get();
        Optional<OtpToken> otpTokenOpt = otpTokenRepository.findByOtp(otp);
        if (otpTokenOpt.isEmpty()) return false;
        OtpToken otpToken = otpTokenOpt.get();
        if (!otpToken.getUser().getId().equals(user.getId())) return false;
        if (otpToken.isUsed() || otpToken.getExpireDate().isBefore(LocalDateTime.now())) return false;
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
        return true;
    }
}
