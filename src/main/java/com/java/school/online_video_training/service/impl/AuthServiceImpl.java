package com.java.school.online_video_training.service.impl;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.school.online_video_training.config.jwt.LoginRequest;
import com.java.school.online_video_training.config.jwt.LoginResponse;
import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.config.security.JwtUtils;
import com.java.school.online_video_training.config.security.RoleEnum;
import com.java.school.online_video_training.config.security.SecurityConstants;
import com.java.school.online_video_training.dto.SignupRequest;
import com.java.school.online_video_training.entity.Role;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.exception.ApiException;
import com.java.school.online_video_training.repository.RoleRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.AuthService;
import com.java.school.online_video_training.service.EmailService;
import com.java.school.online_video_training.service.UserSecurityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final UserSecurityService securityService;

	private final PasswordEncoder passwordEncoder;
	
	private final EmailService emailService;

	private final AuthenticationManager authenticationManager;

	private final JwtUtils jwtUtils;

	@Override
	@Transactional
	public String createUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already taken!");
		}

		// Create new user's account
		User user = new User(signUpRequest.getId(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				passwordEncoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.USER.name())
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role is not found!"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				Role roleEntity = roleRepository.findByName(role)
						.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, role + " Role is not found!"));
				roles.add(roleEntity);
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
		return jwtUtils.generateJwtToken(signUpRequest.getEmail(), buildAuthorityNames(roles));
		/*
		 * Set<String> authorities = roles.stream() .map(Role::getName)
		 * .collect(Collectors.toSet());
		 * 
		 * return jwtUtils.generateJwtToken(signUpRequest.getEmail(), new
		 * ArrayList<>(authorities));
		 */
//		return jwtUtils.generateJwtToken(signUpRequest.getUsername());
	}

	private List<String> buildAuthorityNames(Set<Role> roles) {
		Set<String> names = new LinkedHashSet<>();
		roles.stream().flatMap(role -> role.getPermissions().stream().map(p -> p.getName())).forEach(names::add);
		roles.stream().map(role -> "ROLE_" + role.getName()).forEach(names::add);
		return new ArrayList<>(names);
	}

	@Override
	public LoginResponse authenticateUser(LoginRequest loginRequest) {

		User user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		// Check email verification
		if (!user.isEnabled()) {
		    throw new ApiException(
		        HttpStatus.UNAUTHORIZED,
		        "Please verify your email before logging in."
		    );
		}

		// Check if account is locked
		if (!user.isAccountNonLocked()) {

			if (!securityService.unlockWhenTimeExpired(user)) {

				LocalDateTime unlockTime = user.getLockTime().plusMinutes(SecurityConstants.LOCK_TIME_DURATION_MINUTES);

				long minutesLeft = Math.max(1, Duration.between(LocalDateTime.now(), unlockTime).toMinutes());

				throw new LockedException("Account locked. Try again in " + minutesLeft + " minute(s).");
			}
		}

		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			// Login success
			securityService.resetFailedAttempts(loginRequest.getEmail());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			AuthUser userPrincipal = (AuthUser) authentication.getPrincipal();

			List<String> authorities = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.toList();

			String token = jwtUtils.generateJwtToken(userPrincipal.getEmail(), authorities);

			Set<String> roles = userPrincipal.getRoles().stream().map(Role::getName)
					.collect(Collectors.toCollection(LinkedHashSet::new));

			return new LoginResponse(token, userPrincipal.getUsername(), userPrincipal.getEmail(), roles);

		} catch (BadCredentialsException ex) {

//			increaseFailedAttempts(loginRequest.getEmail());
			securityService.increaseFailedAttempts(loginRequest.getEmail());

			throw ex;
		}
	}

	@Override
	@Transactional
	public void forgotPassword(String email) {

		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null) {
			return;
		}

		String token = generateResetToken();

		user.setResetPasswordToken(token);

		user.setResetPasswordExpiry(LocalDateTime.now().plusMinutes(15));

		userRepository.save(user);

		emailService.sendResetPasswordEmail(user);
	}

	@Override
	@Transactional
	public void resetPassword(String token, String password) {
		  log.info("Reset token received: {}", token);


		User user = userRepository.findByResetPasswordToken(token)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid token"));
		
		  log.info("User found: {}", user.getEmail());
		    log.info("Expiry time: {}", user.getResetPasswordExpiry());
		    log.info("Current time: {}", LocalDateTime.now());

		if (user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
			log.error("Token expired");

			user.setResetPasswordToken(null);
			user.setResetPasswordExpiry(null);

			userRepository.save(user);

			throw new ApiException(HttpStatus.BAD_REQUEST, "Reset link expired");
		}

		user.setPassword(passwordEncoder.encode(password));

		user.setResetPasswordToken(null);

		user.setResetPasswordExpiry(null);

		userRepository.save(user);
		log.info("Password reset successful");
	}

	private String generateResetToken() {

		SecureRandom random = new SecureRandom();

		byte[] bytes = new byte[32];

		random.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	/*
	 * @Override public LoginResponse authenticateUser(LoginRequest loginRequest) {
	 * Authentication authentication = authenticationManager.authenticate( new
	 * UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
	 * loginRequest.getPassword()));
	 * 
	 * SecurityContextHolder.getContext().setAuthentication(authentication);
	 * AuthUser userPrincipal = (AuthUser) authentication.getPrincipal();
	 * 
	 * List<String> authorities = userPrincipal.getAuthorities().stream()
	 * .map(grantedAuthority -> grantedAuthority.getAuthority())
	 * .collect(Collectors.toList());
	 * 
	 * // Pass username and authorities to JWT generator // return
	 * jwtUtils.generateJwtToken(userPrincipal.getUsername(), authorities); String
	 * token = jwtUtils.generateJwtToken( userPrincipal.getEmail(), authorities );
	 * 
	 * Set<String> roles = userPrincipal.getRoles().stream() .map(Role::getName)
	 * .collect(Collectors.toCollection(LinkedHashSet::new));
	 * 
	 * return new LoginResponse( token, userPrincipal.getUsername(),
	 * userPrincipal.getEmail(), roles );
	 * 
	 * // return jwtUtils.generateJwtToken(userPrincipal.getUsername()); }
	 */

	/*
	 * @Override public String createUser(SignupRequest signUpRequest) { if
	 * (userRepository.existsByUsername(signUpRequest.getUsername())) { throw new
	 * ApiException(HttpStatus.BAD_REQUEST, "Username is already taken!"); }
	 * 
	 * if (userRepository.existsByEmail(signUpRequest.getEmail())) { throw new
	 * ApiException(HttpStatus.BAD_REQUEST, "Email is already taken!"); }
	 * 
	 * // Create new user's account User user = new
	 * User(signUpRequest.getUsername(), signUpRequest.getEmail(),
	 * passwordEncoder.encode(signUpRequest.getPassword()));
	 * 
	 * Set<String> strRoles = signUpRequest.getRoles(); Set<Role> roles = new
	 * HashSet<>();
	 * 
	 * if (strRoles == null) { Role userRole =
	 * roleRepository.findByName(RoleEnum.USER.name()) .orElseThrow(() -> new
	 * ApiException(HttpStatus.BAD_REQUEST, "Role is not found!"));
	 * roles.add(userRole); } else { strRoles.forEach(role -> { Role adminRole =
	 * roleRepository.findByName(role) .orElseThrow(() -> new
	 * ApiException(HttpStatus.BAD_REQUEST, role + " Role is not found!"));
	 * roles.add(adminRole); }); }
	 * 
	 * user.setRoles(roles); userRepository.save(user); Set<String> authorities =
	 * roles.stream() .map(Role::getName) .collect(Collectors.toSet());
	 * 
	 * return jwtUtils.generateJwtToken(signUpRequest.getEmail(), new
	 * ArrayList<>(authorities)); // return
	 * jwtUtils.generateJwtToken(signUpRequest.getUsername()); }
	 */
}
