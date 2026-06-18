package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.java.school.online_video_training.enitity_enum.CourseType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseDTO {
	private Long categoryId;
	@NotNull
	private String name;
//	@JsonAlias({ "authorId", "author_id" })
//	private Long authorId;
	private String imageCover;
	private BigDecimal price;
	@NotNull
    private CourseType courseType;
	private String courseDescription;
}
