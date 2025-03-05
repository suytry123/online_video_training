package com.java.school.online_video_training.config.security;

import java.util.Optional;

public interface UserService {
	Optional<AuthUser> findUserByUsername(String username);
}
