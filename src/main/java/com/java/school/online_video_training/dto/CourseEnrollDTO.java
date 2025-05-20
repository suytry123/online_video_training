package com.java.school.online_video_training.dto;

import lombok.Data;

@Data
public class CourseEnrollDTO {
    private Long courseId;
    private Long userId; // the student who wants to enroll
}
