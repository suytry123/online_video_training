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
	private Long categoryId;
	private String name;
//	@JsonAlias({ "authorId", "author_id" })
//	private Long authorId;
	private String imageCover;
	private BigDecimal price;
	private String courseDescription;
}
