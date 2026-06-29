package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import com.java.school.online_video_training.enums.CourseType;

import lombok.Data;

@Data
public class CourseResponseDTO {
	private Long id;
	private String name;
	private BigDecimal price;
	private String courseDescription;

	private Long categoryId; 
	private String categoryName;

	private Long authorId;
	private String authorName;
	private String imageCover;
    private CourseType courseType;
}
