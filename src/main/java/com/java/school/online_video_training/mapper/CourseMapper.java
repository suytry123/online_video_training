package com.java.school.online_video_training.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.service.CategoryService;

@Mapper(componentModel = "spring", uses = { CategoryService.class, VideoMapper.class })
public interface CourseMapper {

//	@Mapping(target = "category", source = "categoryId")
//	Course toCourse(CourseDTO courseDTO);
//	
//	@Mapping(target = "categoryId", source = "category_id.id")
//	CourseDTO toCourseDTO(Course course);

//	@Mapping(target = "author", source = "authorId")
//	@Mapping(target = "category_id", source = "categoryId")
//	Course toCourse(CourseDTO courseDTO);
//
//	@Mapping(target = "categoryId", source = "category_id.id")
//	CourseDTO toCourseDTO(Course course);
	
	@Mapping(target = "author_id", source = "authorId")
    @Mapping(target = "category_id", source = "categoryId")
    Course toCourse(CourseDTO courseDTO);

    @Mapping(target = "categoryId", source = "category_id.id")
    @Mapping(target = "authorId", source = "author_id.id")
    CourseDTO toCourseDTO(Course course);

	@Mapping(target = "category_id", source = "categoryId")
	Course toCourse(CourseSummaryDTO dto);

	@Mapping(target = "categoryId", source = "category_id.id")
	@Mapping(target = "authorName", source = "author_id.username")
	CourseSummaryDTO toCourseSummaryDTO(Course course);

	@Mapping(target = "categoryId", source = "category_id.id")
	@Mapping(target = "authorName", source = "author_id.username")
	@Mapping(target = "videos", source = "videos")
	CourseDetailDTO toCourseDetailDTO(Course course);

	List<CourseSummaryDTO> toCourseSummaryDTOs(List<Course> courses);
	
	 default User map(Long authorId) {
	        if (authorId == null) return null;
	        User user = new User();
	        user.setId(authorId);
	        return user;
	    }
}
