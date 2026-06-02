package com.java.school.online_video_training.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.java.school.online_video_training.config.security.SecurityConstants;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.UserSecurityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSecurityServiceImpl implements UserSecurityService {
	
	private final UserRepository userRepository;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void increaseFailedAttempts(String email) {


		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null) {
			return;
		}

		int attempts = user.getFailedAttempts() + 1;
		user.setFailedAttempts(attempts);

		if (attempts >= SecurityConstants.MAX_FAILED_ATTEMPTS) {
			user.setAccountNonLocked(false);
			user.setLockTime(LocalDateTime.now());
		}
		userRepository.save(user);
	}

	@Override
	public void resetFailedAttempts(String email) {

		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null) {
			return;
		}

		if (user.getFailedAttempts() > 0) {
			user.setFailedAttempts(0);
			userRepository.save(user);
		}
	}

	@Override
	public boolean unlockWhenTimeExpired(User user) {

		if (user.getLockTime() == null) {
			return false;
		}

		LocalDateTime unlockTime = user.getLockTime().plusMinutes(SecurityConstants.LOCK_TIME_DURATION_MINUTES);

		if (LocalDateTime.now().isAfter(unlockTime)) {

			user.setAccountNonLocked(true);

			user.setFailedAttempts(0);

			user.setLockTime(null);

			userRepository.save(user);

			return true;
		}

		return false;
	}
}
