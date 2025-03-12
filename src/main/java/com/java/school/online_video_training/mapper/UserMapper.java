package com.java.school.online_video_training.mapper;

import org.mapstruct.Mapper;

import com.java.school.online_video_training.dto.UserDTO;
import com.java.school.online_video_training.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	User toUser(UserDTO userDTO);
	UserDTO toUserDTO(User user);
	
}
