package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.service.CategoryService;

@Mapper( componentModel = "spring", uses = {CategoryService.class} )
public interface CourseMapper {
	
	@Mapping(target = "category", source = "categoryId")
	Course toCourse(CourseDTO courseDTO);
	
	@Mapping(target = "categoryId", source = "category.id")
	CourseDTO toCourseDTO(Course course);
}
