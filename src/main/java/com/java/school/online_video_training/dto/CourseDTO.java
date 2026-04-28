package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
	private Long categoryId;
	private String name;
	private Long authorId;
	private BigDecimal price;
}
