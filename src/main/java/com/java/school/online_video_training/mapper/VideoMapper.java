package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.service.CourseService;

@Mapper(componentModel = "spring", uses = {CourseService.class})
public interface VideoMapper {
	
	@Mapping(target = "course", source = "courseId")
	Video toVideo(VideoDTO videoDTO);
	
	@Mapping(target = "courseId", source = "course.id")
	VideoDTO toVideoDTO(Video video);
	
	default Course mapCourse(Long courseId) {
	    if (courseId == null) return null;
	    Course course = new Course();
	    course.setId(courseId);
	    return course;
	}
}
