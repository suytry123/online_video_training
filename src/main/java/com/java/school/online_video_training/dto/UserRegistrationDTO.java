package com.java.school.online_video_training.dto;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationDTO {
	
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    // Make email optional for author applications
    @Email(message = "Invalid email format")
    private String email;

    // Make password optional for author applications
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private Set<String> roleNames;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Pattern(regexp = "^[0-9+]{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String education;
    
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;
    
    @Size(max = 500, message = "Author bio must not exceed 500 characters")
    private String authorBio;
    
    @Size(max = 200, message = "Author expertise must not exceed 200 characters")
    private String authorExpertise;
    
    private boolean wantToBeAuthor = false;

    @JsonCreator
    public UserRegistrationDTO(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("roleNames") Set<String> roleNames,
            @JsonProperty("gender") String gender,
            @JsonProperty("phoneNumber") String phoneNumber,
            @JsonProperty("education") String education,
            @JsonProperty("address") String address) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleNames = roleNames;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.education = education;
        this.address = address;
    }
}