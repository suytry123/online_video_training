package com.java.school.online_video_training.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.dto.VideoDTO;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.mapper.VideoMapper;
import com.java.school.online_video_training.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/videos")
public class VideoController {
	private final VideoService videoService;
	private final VideoMapper videoMapper;

	@PreAuthorize("hasAuthority('video:write')")
	@PostMapping
	// @ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createVideo(@RequestBody VideoDTO videoDTO) {
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.createVideo(video);
		return ResponseEntity.ok(videoMapper.toVideoDTO(video));
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/{id}")
	public ResponseEntity<?> getVideoById(@PathVariable Long id) {
		Video byId = videoService.getVideoById(id);
		return ResponseEntity.ok(videoMapper.toVideoDTO(byId));
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping
	public ResponseEntity<?> getCourses(@RequestParam Map<String, String> video) {
		Page<Video> video1 = videoService.getVideos(video);

		PageDTO dto = new PageDTO(video1);

		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAuthority('video:write')")
	@PutMapping("{id}")
	public ResponseEntity<?> updateVideo(@PathVariable Long id, @RequestBody VideoDTO videoDTO) {
		Video video = videoMapper.toVideo(videoDTO);
		video = videoService.updateVideo(id, video);
		return ResponseEntity.ok(video);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
		videoService.deleteVideo(id);
		return ResponseEntity.ok().build();
	}
	
/*
	@PreAuthorize("hasAuthority('video:write')")
	@PostMapping("/upload/{id}")
	public ResponseEntity<?> uploadPicture(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws Exception {
		if (file.isEmpty()) {
			throw new RuntimeException("Please load a file");
		}

		try {
			videoService.saveImage(id, file);
			log.info("your image save successful.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Save image Error", e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("path/{path}")
	public ResponseEntity<?> getById(@PathVariable String path) throws Exception {
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("File name cannot be null or empty.");
		}

		Path filePath = Paths.get("src", "main", "resources", "file-repository", path);

		if (!Files.exists(filePath)) {
			return ResponseEntity.notFound().build();
		}

		byte[] fileBytes = Files.readAllBytes(filePath);
		// byte[] byPath = videoService.getByPath(path);

		String contentType = Files.probeContentType(filePath);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
	}

	@PreAuthorize("hasAuthority('video:write')")
	@PutMapping("update/{path}")
	public ResponseEntity<?> updateVideoImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		try {
			// Call the service method to update the video image (path)
			videoService.updateImage(id, file);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video image.");
		}
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/images")
	public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) {
		Path directoryPath = Paths.get("src", "main", "resources", "file-repository");
		log.info("Directory Path: {}", directoryPath);

		try {
			Page<String> images = videoService.getImages(image);
			Path imagePath = Paths.get(directoryPath.toString(), images.getContent().get(0));
			byte[] imageBytes = Files.readAllBytes(imagePath);
			PageDTO dto = new PageDTO(images);
			log.info("Images retrieved successfully");
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			log.error("Failed to get images", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve images: " + e.getMessage());
		}
	}

	@DeleteMapping("/delete/{url}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) throws Exception {
		try {
			// Call the service method to delete the file
			videoService.deleteImageById(id);
			log.info("Delete successfully: " + id);
			return ResponseEntity.ok("Image deleted successfully.");
		} catch (Exception e) {
			// Log the error and send the failure response
			log.error("Delete failed: " + e.getMessage());
			return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
		}
	}*/
	
	@PreAuthorize("hasAuthority('video:write')")
	@PostMapping("/upload/{id}")
	public ResponseEntity<?> uploadPicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
	    if (file.isEmpty()) {
	        throw new RuntimeException("Please load a file");
	    }
	    videoService.saveImage(id, file);
	    log.info("Image saved successfully.");
	    return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/image/{id}")
	public ResponseEntity<?> getImageCoverById(@PathVariable Long id) throws Exception {
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		 log.info("Authorities: " + auth.getAuthorities()); // Debug: print current user's authorities
		
	    byte[] fileBytes = videoService.getImageCoverById(id);
	    String contentType = "image/jpeg"; // Or detect dynamically if needed
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
	}

	@PreAuthorize("hasAuthority('video:write')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateVideoImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
	    try {
	        videoService.updateImage(id, file);
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video image.");
	    }
	}

	/*
	//@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/images")
	public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) {
	    Path directoryPath = Paths.get("src", "main", "resources", "file-repository");
	    log.info("Directory Path: {}", directoryPath);

	    try {
	        Page<String> images = videoService.getImages(image);
	        Path imagePath = Paths.get(directoryPath.toString(), images.getContent().get(0));
	        byte[] imageBytes = Files.readAllBytes(imagePath);
	        PageDTO dto = new PageDTO(images);
	        log.info("Images retrieved successfully");
	        return ResponseEntity.ok(dto);
	    } catch (Exception e) {
	        log.error("Failed to get images", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to retrieve images: " + e.getMessage());
	    }
	}*/
	
	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/images")
	public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) {
	    try {
	        Page<Map<String, String>> images = videoService.getImages(image);
	        PageDTO dto = new PageDTO(images);
	        log.info("Image metadata retrieved successfully");
	        return ResponseEntity.ok(dto);
	    } catch (Exception e) {
	        log.error("Failed to get image metadata", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to retrieve image metadata: " + e.getMessage());
	    }
	}
	
	@PreAuthorize("hasAuthority('video:write')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) throws Exception {
	    try {
	        videoService.deleteImageById(id);
	        log.info("Delete successfully: " + id);
	        return ResponseEntity.ok("Image deleted successfully.");
	    } catch (Exception e) {
	        log.error("Delete failed: " + e.getMessage());
	        return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
	    }
	}

	@PreAuthorize("hasAuthority('video:write')")
	@PostMapping("/{id}/linkVideo")
	// @ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createLink(@PathVariable Long id, @RequestBody List<String> link) {
		try {
			videoService.videoLink(id, link); // Call the service method to update the video link
			return ResponseEntity.ok("Video link updated successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to update video link: " + e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/{id}/getLink")
	public ResponseEntity<?> getLink(@PathVariable Long id) {
		String link = videoService.getLink(id);
		return ResponseEntity.ok(link);
	}

	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/links")
	public ResponseEntity<?> getLinks(@RequestParam Map<String, String> link) {
		Page<String> video1 = videoService.getLinks(link);

		PageDTO dto = new PageDTO(video1);

		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAuthority('video:write')")
	@PutMapping("{id}/updateLink")
	public ResponseEntity<?> updateVideoLink(@PathVariable Long id, @RequestBody List<String> link) {
		videoService.updateLink(id, link);
		return ResponseEntity.ok(link);
	}

	@PreAuthorize("hasAuthority('video:write')")
	@DeleteMapping("/{id}/deleteLink")
	public ResponseEntity<String> deleteLink(@PathVariable Long id) {
		videoService.deleteLink(id);
		return ResponseEntity.ok("Link deleted successfully");
	}
	
	@PreAuthorize("hasAuthority('video:read')")
	@GetMapping("/user-access")
	public ResponseEntity<?> getVideosForUser(@RequestParam Long courseId, @RequestParam Long userId, @RequestParam Map<String, String> params) {
		Page<VideoDTO> videos = videoService.getVideosForUser(courseId, userId, params);
		PageDTO dto = new PageDTO(videos);
		return ResponseEntity.ok(dto);
	}

}
