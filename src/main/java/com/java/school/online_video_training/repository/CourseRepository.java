package com.java.school.online_video_training.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.java.school.online_video_training.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course>{
//	List<Course> findByCategoryId(Long categoryId);
	Optional<Course> findByImageCover(String path);
	List<Course> findByIsDeletedFalse();
	List<Course> findByIsDeletedTrue();
}
