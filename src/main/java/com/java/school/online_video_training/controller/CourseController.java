package com.java.school.online_video_training.controller;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.config.security.AuthUser;
import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseEnrollDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.CourseSummaryDTO;
import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.service.CourseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/courses")
public class CourseController {
	private final CourseService courseService;
	private final CourseRepository courseRepository;
//	private final CourseMapper courseMapper;

	@PreAuthorize("hasAuthority('course:write')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseDTO courseDTO) {
        CourseResponseDTO created = courseService.create(courseDTO);
        return ResponseEntity.ok(created);
    }
	
//	@PreAuthorize("hasAuthority('course:read')")
//	@GetMapping("{id}")
//	public ResponseEntity<?> getById(@PathVariable("id") Long id){
//		Course course = courseService.getById(id);
//		return ResponseEntity.ok(courseMapper.toCourseDTO(course));
//	}
//
//	@PreAuthorize("hasAuthority('course:read')")
//	@GetMapping
//	public ResponseEntity<?> getCourses(@RequestParam Map<String, String> course) {
//		Page<Course> courses = courseService.getCourses(course);
//
//		PageDTO dto = new PageDTO(courses);
//
//		return ResponseEntity.ok(dto);
//	}

	@PreAuthorize("hasAuthority('course:read')")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
		CourseResponseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PreAuthorize("hasAuthority('course:write')")
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long courseId, @RequestBody CourseDTO courseUpdate) {
    	CourseResponseDTO updated = courseService.update(courseId, courseUpdate);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAuthority('course:write')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long courseId) {
        courseService.delete(courseId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('course:read')")
    @GetMapping
    public ResponseEntity<?> getCourses(@RequestParam Map<String, String> course) {
        Page<CourseResponseDTO> courses = courseService.getCourses(course);
        PageDTO dto = new PageDTO(courses);
        return ResponseEntity.ok(dto);
    }

//    @PreAuthorize("hasAuthority('course:read')")
    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getCourseDetail(@PathVariable Long id){
        CourseDetailDTO courseDetail = courseService.getCourseDetail(id);
        return ResponseEntity.ok(courseDetail);
    }
    
    @PreAuthorize("hasAuthority('course:read')")
	@GetMapping("/trash")
	public ResponseEntity<?> getTrash() {

		return ResponseEntity.ok(courseService.getTrash());

	}

    @PreAuthorize("hasAuthority('course:write')")
	@PutMapping("/{id}/restore")
	public ResponseEntity<?> restore(@PathVariable Long id) {

		courseService.restore(id);

		return ResponseEntity.ok().build();

	}


	/*@PreAuthorize("isAuthenticated()")
	@PostMapping("/{courseId}/view")
	public ResponseEntity<?> addView(@PathVariable Long courseId, Authentication authentication) {

		AuthUser user = (AuthUser) authentication.getPrincipal();

		Long userId = user.getId();

		courseService.addView(courseId, userId);

		return ResponseEntity.ok("View counted successfully");
	}*/
    
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{courseId}/view")
	public ResponseEntity<?> addView(@PathVariable Long courseId, Authentication authentication) {

		AuthUser user = (AuthUser) authentication.getPrincipal();

		Long userId = user.getId();

		courseService.addView(courseId, userId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "View counted successfully");

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{courseId}/like")
	public ResponseEntity<?> likeCourse(@PathVariable Long courseId, Authentication authentication) {

		AuthUser user = (AuthUser) authentication.getPrincipal();

		Long userId = user.getId();

		courseService.likeCoruse(courseId, userId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Course liked successfully");

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{courseId}/like")
	public ResponseEntity<?> unlikeCourse(@PathVariable Long courseId, Authentication authentication) {

		AuthUser user = (AuthUser) authentication.getPrincipal();

		Long userId = user.getId();

		courseService.unlikeCourse(courseId, userId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Course unliked successfully");

		return ResponseEntity.ok(response);
	}
	

    
    
//    @PostMapping("/{id}/view")
//    public ResponseEntity<?> increaseView(@PathVariable Long id) {
//
//        courseService.increaseView(id);
//
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/{id}/like")
//    public ResponseEntity<?> likeCourse(@PathVariable Long id) {
//
//        courseService.likeCourse(id);
//
//        return ResponseEntity.ok().build();
//    }

    
    @GetMapping("/summary")
    public ResponseEntity<List<CourseSummaryDTO>> getAllCourses() {
        List<CourseSummaryDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
	
	@PreAuthorize("hasAuthority('course:read')")
	@PostMapping("/enroll")
	public ResponseEntity<?> enroll(@RequestBody CourseEnrollDTO enrollDTO) {
	    courseService.enroll(enrollDTO.getCourseId(), enrollDTO.getUserId());
	    return ResponseEntity.ok("Enrollment request submitted to the author.");
	}
	
	@PreAuthorize("hasAuthority('course:write')")
	@PostMapping("/upload/{id}")
	public ResponseEntity<?> uploadPicture(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws Exception {

		if (file.isEmpty()) {
			throw new RuntimeException("Please load a file");
		}

		courseService.saveImage(id, file);

		log.info("Image saved successfully.");

		return ResponseEntity.ok().build();
	}

	@GetMapping("/image/{id}")
	public ResponseEntity<?> getImageCoverById(@PathVariable Long id) throws Exception {

		Course course = courseRepository.findById(id).orElseThrow(() -> new FileNotFoundException("Course not found"));

		Path path = Paths.get("uploads", "courses", course.getImageCover());

		byte[] fileBytes = Files.readAllBytes(path);

		String contentType = Files.probeContentType(path);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
	}

	@PreAuthorize("hasAuthority('course:write')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateVideoImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

		try {

			courseService.updateImage(id, file);

			return ResponseEntity.ok().build();

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update course image.");
		}
	}

	@GetMapping("/images")
	public ResponseEntity<?> getImages(@RequestParam Map<String, String> image) {

		try {

			Page<Map<String, String>> images = courseService.getImages(image);

			PageDTO dto = new PageDTO(images);

			log.info("Image metadata retrieved successfully");

			return ResponseEntity.ok(dto);

		} catch (Exception e) {

			log.error("Failed to get image metadata", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve image metadata: " + e.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('course:write')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteImageById(@PathVariable Long id) throws Exception {

		try {

			courseService.deleteImageById(id);

			log.info("Delete successfully: " + id);

			return ResponseEntity.ok("Image deleted successfully.");

		} catch (Exception e) {

			log.error("Delete failed: " + e.getMessage());

			return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
		}
	}

//	@GetMapping("/image/{id}")
//	public ResponseEntity<?> getImageCoverById(@PathVariable Long id) throws Exception {
//		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		 log.info("Authorities: " + auth.getAuthorities()); // Debug: print current user's authorities
//		
//	    byte[] fileBytes = courseService.getImageCoverById(id);
//	    String contentType = "image/jpeg"; // Or detect dynamically if needed
//	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(fileBytes);
//	}

	
}
