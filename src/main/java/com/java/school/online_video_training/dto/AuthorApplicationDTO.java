package com.java.school.online_video_training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.java.school.online_video_training.enitity_enum.Gender;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorApplicationDTO {

	@NotBlank(message = "Education is required")
	@Size(max = 255)
	private String education;

	@NotBlank(message = "Address is required")
	@Size(max = 500)
	private String address;

    @NotBlank(message = "Author bio is required")
    @Size(max = 500, message = "Author bio cannot exceed 500 characters")
    private String authorBio;

    @NotBlank(message = "Author expertise is required")
    @Size(max = 200, message = "Author expertise cannot exceed 200 characters")
    private String authorExpertise;
}