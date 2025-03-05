package com.java.school.online_video_training.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.school.online_video_training.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String name);
}
