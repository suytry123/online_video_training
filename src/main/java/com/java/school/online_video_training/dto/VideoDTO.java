package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class VideoDTO {
    private Long id;     
    private Long courseId;
    private String title;
    private String description;
}
