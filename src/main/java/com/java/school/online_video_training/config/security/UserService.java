package com.java.school.online_video_training.config.security;

import java.util.Optional;

import com.java.school.online_video_training.dto.UserDTO;

public interface UserService {
	Optional<AuthUser> findUserByUsername(String username);
	void rigisterUser(UserDTO userDTO);
}
