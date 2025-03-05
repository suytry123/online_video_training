package com.java.school.online_video_training.config.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceFakeImpl implements UserService{
	
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public Optional<AuthUser> findUserByUsername(String username) {
		List<AuthUser> users = List.of(
				new AuthUser("seyha", passwordEncoder.encode("seyha67822"), RoleEnum.AUTHOR.getAuthorities(), true, true, true, true),
				new AuthUser("kanha", passwordEncoder.encode("kanha67822"), RoleEnum.ADMIN.getAuthorities(), true, true, true, true)
				);
		return users.stream()
			.filter(user -> user.getUsername().equals(username))
			.findFirst();
	}

}
