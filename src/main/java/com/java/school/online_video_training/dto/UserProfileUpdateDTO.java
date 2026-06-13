package com.java.school.online_video_training.dto;

import javax.validation.constraints.Size;

import com.java.school.online_video_training.enitity_enum.Gender;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {

    @Size(max = 20)
    private String phoneNumber;

    private Gender gender;
}