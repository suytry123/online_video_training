package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CourseDTO {
	private Long categoryId;
	private String name;
	private Long authorId;
	private BigDecimal price;
}
