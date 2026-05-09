package com.java.school.online_video_training.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.school.online_video_training.entity.CourseLike;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {

	boolean existsByUserIdAndCourseId(Long userId, Long courseId);

	Optional<CourseLike> findByUserIdAndCourseId(Long userId, Long courseId);
}
