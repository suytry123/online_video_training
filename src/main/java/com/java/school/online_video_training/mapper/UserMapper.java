package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.dto.UserPhotoDTO;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	// Map DTO to Entity (UserRegistrationDTO to User)
	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(userDTO.getPassword()))")
	@Mapping(target = "enabled", constant = "false")  // Set enabled as false by default
    User toUser(UserRegistrationDTO userDTO);

    // Map Entity to DTO (User to UserRegistrationDTO)
    @Mapping(target = "password", ignore = true)  // Ignore password when mapping back to DTO
    UserRegistrationDTO toUserDTO(UserRegistrationDTO registrationDTO);
	
    UserPhotoDTO toPhotoDTO(User user);
}
