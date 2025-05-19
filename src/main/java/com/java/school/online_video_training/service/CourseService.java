package com.java.school.online_video_training.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.entity.Course;

public interface CourseService {
//	Course create(CourseDTO courseDTO);
	Course create(Course course);
	Course getById(Long id);
	Page<Course> getCourses(Map<String, String> course);
	Course update(Long id, Course courseUpdate);
	void delete(Long id);
	List<CourseSummaryDTO> getAllCourses();
	CourseDetailDTO getCourseDetail(Long courseId);
}
