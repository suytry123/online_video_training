package com.java.school.online_video_training.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class VideoDTO {
	private Long id;
    private Long courseId;
    private String title;
    private String description;
    private List<String> videoLink; 
}
