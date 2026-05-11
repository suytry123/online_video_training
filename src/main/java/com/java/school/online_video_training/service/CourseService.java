package com.java.school.online_video_training.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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
	void addView(Long courseId, Long userId);
	void likeCoruse(Long courseId, Long userId);
	void unlikeCourse(Long courseId, Long userId);
	void saveImage(Long id, MultipartFile file) throws Exception;
	byte[] getImageCoverById(Long id) throws Exception;
	void updateImage(Long id, MultipartFile file) throws Exception;
	Page<Map<String, String>> getImages(Map<String, String> images);
	void deleteImageById(Long id) throws Exception;
	List<CourseResponseDTO> getTrash();
	void restore(Long id);

}
