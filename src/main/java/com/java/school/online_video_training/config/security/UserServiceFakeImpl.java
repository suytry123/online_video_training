package com.java.school.online_video_training.config.security;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

import lombok.RequiredArgsConstructor;

//@Service
//@RequiredArgsConstructor
//public class UserServiceFakeImpl implements UserService{
//	
//	private final PasswordEncoder passwordEncoder;
//	
//	@Override
//	public Optional<AuthUser> findUserByUsername(String username) {
//		List<AuthUser> users = List.of(
//				new AuthUser("seyha", passwordEncoder.encode("seyha67822"), RoleEnum.AUTHOR.getAuthorities(), true, true, true, true),
//				new AuthUser("kanha", passwordEncoder.encode("kanha67822"), RoleEnum.ADMIN.getAuthorities(), true, true, true, true)
//				);
//		return users.stream()
//			.filter(user -> user.getUsername().equals(username))
//			.findFirst();
//	}
//
//	@Override
//	public User rigisterUserForm(UserRegistrationDTO userDTO) {
//		
//		return null;
//	}
//
//	@Override
//	public String verification(String token) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void sendVerificationEmail(User user) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
