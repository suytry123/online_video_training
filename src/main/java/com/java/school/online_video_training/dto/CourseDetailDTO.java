package com.java.school.online_video_training.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class CourseDetailDTO {
    private Long id;
    private String name;
    private String authorName;
    private String categoryName;
    private int views;
    private int likes;
    private List<VideoDTO> videos;
//    @JsonAlias({ "imageCover", "image_cover" })
//    private String imageCover;
}