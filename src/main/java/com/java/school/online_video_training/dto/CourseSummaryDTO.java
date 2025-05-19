package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class CourseSummaryDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String authorName;
    private int views;
    private int likes;
}
