package com.java.school.online_video_training.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;

public interface CourseService {
//	Course create(CourseDTO courseDTO);
	CourseResponseDTO create(CourseDTO courseDTO);
	CourseResponseDTO getCourseById(Long id);
	Page<CourseResponseDTO> getCourses(Map<String, String> course);
	CourseResponseDTO update(Long id, CourseDTO courseUpdate);
	void delete(Long id);
	List<CourseSummaryDTO> getAllCourses();
	CourseDetailDTO getCourseDetail(Long courseId);
	void enroll(Long courseId, Long userId);
}
