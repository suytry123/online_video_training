package com.java.school.online_video_training.dto;

import java.util.List;

import lombok.Data;

@Data
public class VideoDTO {
	
	private Long courseId;
	private String title;
	private String description;
	private List<String> videoLink;
	private String imageCover;
	
}
