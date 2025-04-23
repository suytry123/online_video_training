package com.java.school.online_video_training.dto;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationDTO {
	
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

	
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @JsonProperty("password")
    private String password;

    @JsonProperty("roleNames")
    private Set<String> roleNames;

    @NotBlank(message = "Gender is required")
    @JsonProperty("gender")
    private String gender;

    @Pattern(regexp = "^[0-9+]{9,15}$", message = "Invalid phone number format")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("education")
    private String education;

    @JsonProperty("address")
    private String address;
}