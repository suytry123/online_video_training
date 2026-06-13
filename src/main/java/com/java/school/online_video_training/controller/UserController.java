package com.java.school.online_video_training.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.config.security.UserService;
import com.java.school.online_video_training.dto.ApiResponse;
import com.java.school.online_video_training.dto.AuthorApplicationDTO;
import com.java.school.online_video_training.dto.AuthorApplicationResponseDTO;
import com.java.school.online_video_training.dto.MessageResponse;
import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.dto.SignupUser;
import com.java.school.online_video_training.dto.UserPhotoDTO;
import com.java.school.online_video_training.dto.UserProfileDTO;
import com.java.school.online_video_training.dto.UserProfileUpdateDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/author-applications")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<AuthorApplicationResponseDTO> applyForAuthor(@Valid @RequestBody AuthorApplicationDTO dto) {

		return ResponseEntity.ok(userService.submitAuthorApplication(dto));
	}

	@GetMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestParam String token) {
		String message = userService.verifyEmail(token);
		return ResponseEntity.ok(message);
	}

	@PostMapping("/author-applications/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> approveAuthor(@PathVariable Long id) {

	    String result = userService.approveAuthorApplication(id);

	    return ResponseEntity.ok(result);
	}

	@PostMapping("/author-applications/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> rejectAuthor(@PathVariable Long id) {

	    String result = userService.rejectAuthorApplication(id);

	    return ResponseEntity.ok(result);
	}
	
	@GetMapping("/author-applications")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<AuthorApplicationResponseDTO>> getApplications(
			@RequestParam Map<String, String> params) {

		return ResponseEntity.ok(userService.getAuthorApplications(params));
	}
	
	@PostMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> uploadPhoto(@PathVariable Long userId, @RequestPart("photo") MultipartFile photo) {
		try {
			UserPhotoDTO user = userService.uploadPhoto(userId, photo);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo upload failed");
		}
	}

	/*@GetMapping("/photo/{userId}")
	public ResponseEntity<?> getPhoto(@PathVariable Long userId) {
		try {
			UserPhotoDTO user = userService.getPhotoById(userId);
			String photo = user.getPhotoUrl();
			if (photo == null) {
				return ResponseEntity.notFound().build();
			}
			Path filePath = Paths.get("uploads", "users", photo);
			if (!Files.exists(filePath)) {
				return ResponseEntity.notFound().build();
			}
			String contentType = Files.probeContentType(filePath);
			byte[] fileBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo retrieval failed");
		}
	}*/
	
	@GetMapping("/photo/{userId}")
	public ResponseEntity<byte[]> getPhoto(@PathVariable Long userId) throws IOException {

	    byte[] image = userService.getPhotoContent(userId);
	    if (image == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_JPEG)
	            .body(image);
	}

	@GetMapping("/photos")
	public ResponseEntity<?> getPhotos(@RequestParam Map<String, String> photos) {
		try {
			Page<Map<String, String>> photoMetadata = userService.getPhotoMetadata(photos);
			PageDTO dto = new PageDTO(photoMetadata);
			log.info("Photo metadata retrieved successfully");
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			log.error("Failed to get photo metadata", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve photo metadata: " + e.getMessage());
		}
	}

	@PutMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> updatePhoto(@PathVariable Long userId, @RequestPart("photo") MultipartFile photo) {
		try {
			UserPhotoDTO user = userService.updatePhoto(userId, photo);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo update failed");
		}
	}

	@DeleteMapping("/photo/{userId}")
	@PreAuthorize("hasAuthority('user:write')")
	public ResponseEntity<?> deletePhoto(@PathVariable Long userId) {
		userService.deletePhoto(userId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup_user")
	public ResponseEntity<?> createUserAcc(@Valid @RequestBody SignupUser signupUser) {
		MessageResponse jwt = userService.signupUser(signupUser);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeaders).build();
	}
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(Authentication authentication) {

		AuthUser authUser = (AuthUser) authentication.getPrincipal();

		UserProfileDTO profile = userService.getProfile(authUser.getId());

		return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profile));
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(Authentication authentication,
			@RequestBody UserProfileUpdateDTO dto) {

		AuthUser authUser = (AuthUser) authentication.getPrincipal();

		UserProfileDTO profile = userService.updateProfile(authUser.getId(), dto);

		return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", profile));
	}
}