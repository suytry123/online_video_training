package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.dto.VideoResponseDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.service.CourseService;

@Mapper(componentModel = "spring", uses = {CourseService.class})
public interface VideoMapper {
	
//	@Mapping(target = "course", source = "courseId")
	@Mapping(target = "course", ignore = true)
	@Mapping(target = "videoLink", source = "videoLink")
	Video toVideo(VideoDTO videoDTO);
	
	@Mapping(target = "courseId", source = "course.id")
	@Mapping(target = "videoLink", source = "videoLink")
	VideoResponseDTO toVideoResponseDTO(Video video);
	
	default Course mapCourse(Long courseId) {
	    if (courseId == null) return null;
	    Course course = new Course();
	    course.setId(courseId);
	    return course;
	}
}