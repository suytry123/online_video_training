package com.java.school.online_video_training.config.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.dto.MessageResponse;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserPhotoDTO;
import com.java.school.online_video_training.dto.UserProfileDTO;
import com.java.school.online_video_training.dto.UserProfileUpdateDTO;
import com.java.school.online_video_training.dto.UserRegistrationDTO;
import com.java.school.online_video_training.entity.User;

public interface UserService {
	Optional<AuthUser> findUserByUsername(String username);
	AuthUser findUserByEmail(String email);
	User applyForAuthor(UserRegistrationDTO userDTO);
	String verifyEmail(String token);
	void sendVerificationEmail(User user, String token);
	void sendAuthorConfirmationEmail(User user);
	String handleAuthorApproval(String token);
	String handleAuthorRejection(String token);
	UserPhotoDTO uploadPhoto(Long userId, MultipartFile photo);
	UserPhotoDTO updatePhoto(Long userId, MultipartFile photo);
	UserPhotoDTO getPhotoById(Long userId);
	byte[] getPhotoContent(Long userId) throws IOException;
	//Page<String> getPhoto(Map<String, String> photos);
	Page<Map<String, String>> getPhotoMetadata(Map<String, String> photos);
	void deletePhoto(Long userId);
	//String approveAuthor(String token);
	//String rejectAuthor(String token);
	//for user signup
	MessageResponse signupUser(SignupUser signupUser);
	UserProfileDTO getProfile(Long userId);
    UserProfileDTO updateProfile(Long userId, UserProfileUpdateDTO dto);
}
