package com.java.school.online_video_training.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.config.jwt.LoginRequest;
import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.config.security.JwtUtils;
import com.java.school.online_video_training.config.security.RoleEnum;
import com.java.school.online_video_training.dto.SignupRequest;
import com.java.school.online_video_training.entity.Role;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.repository.RoleRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtUtils jwtUtils;
	
	@Override
	public String createUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already taken!");
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
		        passwordEncoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.USER.name())
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role is not found!"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				Role adminRole = roleRepository.findByName(role)
						.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, role + " Role is not found!"));
				roles.add(adminRole);
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
		return jwtUtils.generateJwtToken(signUpRequest.getUsername());
	}

	@Override
	public String authenticateUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
		        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		    SecurityContextHolder.getContext().setAuthentication(authentication);
		    AuthUser userPrincipal = (AuthUser) authentication.getPrincipal();

		    return jwtUtils.generateJwtToken(userPrincipal.getUsername());
	}
	
	
}
