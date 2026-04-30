package com.java.school.online_video_training.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class VideoDTO {
    private Long id;
    @JsonAlias({ "courseId", "course_id" })
    private Long courseId;
    private String title;
    private String description;
}
