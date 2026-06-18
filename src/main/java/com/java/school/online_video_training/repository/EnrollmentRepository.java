package com.java.school.online_video_training.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.school.online_video_training.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	Optional<Enrollment> findByCourseIdAndUserId(Long courseId, Long userId);

	boolean existsByCourseIdAndUserId(Long courseId, Long userId);
}
