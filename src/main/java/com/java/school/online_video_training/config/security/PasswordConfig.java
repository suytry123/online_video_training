package com.java.school.online_video_training.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		//BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
}
