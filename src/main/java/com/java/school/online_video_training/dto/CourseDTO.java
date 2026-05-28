package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseDTO {
	@JsonAlias({ "categoryId", "category_id" })
	private Long categoryId;
	private String name;
//	@JsonAlias({ "authorId", "author_id" })
//	private Long authorId;
	@JsonAlias({ "imageCover", "image_cover" })
	private String imageCover;
	private BigDecimal price;
	@JsonAlias({ "courseDescription", "course_description" })
	private String courseDescription;
}
