package com.java.school.online_video_training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.school.online_video_training.entity.CourseView;

@Repository
public interface CourseViewRepository extends JpaRepository<CourseView, Long> {

	boolean existsByUserIdAndCourseId(Long courseId, Long userId);

}
