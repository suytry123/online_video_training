package com.java.school.online_video_training.dto;

import java.math.BigDecimal;
import java.util.List;

import com.java.school.online_video_training.enums.CourseType;

import lombok.Data;

@Data
public class CourseDetailDTO {
    private Long id;
    private String name;
    private String authorName;
    private String categoryName;
    private int views;
    private int likes;
	private BigDecimal price;
    private CourseType courseType;
    private List<VideoDTO> videos;
//    private String imageCover;
}