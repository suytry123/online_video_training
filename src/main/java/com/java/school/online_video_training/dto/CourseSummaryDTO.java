package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import com.java.school.online_video_training.enums.CourseType;

import lombok.Data;

@Data
public class CourseSummaryDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String authorName;
    private int views;
    private int likes;
    private String imageCover;
	private BigDecimal price;
	private String courseDescription;
    private CourseType courseType;
}