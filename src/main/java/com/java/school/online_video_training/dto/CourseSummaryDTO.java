package com.java.school.online_video_training.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class CourseSummaryDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String authorName;
    private int views;
    private int likes;
    @JsonAlias({ "imageCover", "image_cover" })
    private String imageCover;
	private BigDecimal price;
}