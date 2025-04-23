package com.java.school.online_video_training.repository;

import com.google.common.base.Optional;
import com.java.school.online_video_training.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository {
	Optional<EmailVerificationToken> findByToken(String token);
}
