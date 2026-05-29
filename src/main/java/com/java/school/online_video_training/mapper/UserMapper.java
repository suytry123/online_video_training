package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.dto.UserPhotoDTO;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	// Map DTO to Entity (UserRegistrationDTO to User)
	// DTO -> Entity
//	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(userDTO.getPassword()))")
	@Mapping(target = "enabled", constant = "false")
	User toUser(UserRegistrationDTO userDTO);

	// Entity -> DTO
	@Mapping(target = "password", ignore = true)
	UserRegistrationDTO toUserDTO(User user);
	
	UserPhotoDTO toPhotoDTO(User user);
	
	AuthUser toAuthUser(User user);

}
