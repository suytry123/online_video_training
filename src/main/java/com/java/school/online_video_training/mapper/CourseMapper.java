package com.java.school.online_video_training.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.entity.Category;
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
	
//	@Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    Course toCourse(CourseDTO courseDTO);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "categoryId", source = "category.id") 
    @Mapping(target = "authorId", source = "author.id")  
    CourseResponseDTO  toCourseDTO(Course course);

    @Mapping(target = "category", source = "categoryId")
    Course toCourse(CourseSummaryDTO dto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "authorName", source = "author.username")
    CourseSummaryDTO toCourseSummaryDTO(Course course);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "videos", source = "videos")
    CourseDetailDTO toCourseDetailDTO(Course course);

    List<CourseSummaryDTO> toCourseSummaryDTOs(List<Course> courses);
	
    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
    
	default User map(Long authorId) {
	     if (authorId == null) return null;
	     User user = new User();
	     user.setId(authorId);
	     return user;
	}
}
