package com.java.school.online_video_training.dto;

import java.util.List;

import lombok.Data;

@Data
public class CourseDetailDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String authorName;
    private int views;
    private int likes;
    private List<VideoDTO> videos;
}
