package com.java.school.online_video_training.dto;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {

    @Size(max = 20)
    private String phoneNumber;

    private String gender;
}