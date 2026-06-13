package com.java.school.online_video_training.dto;

import java.time.LocalDateTime;

import com.java.school.online_video_training.enitity_enum.Gender;

import lombok.Data;

@Data
public class UserProfileDTO {

    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private Gender gender;

    private String photo;

    private LocalDateTime joinDate;
}