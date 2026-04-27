package com.java.school.online_video_training.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.dto.CourseDTO;
import com.java.school.online_video_training.dto.CourseDetailDTO;
import com.java.school.online_video_training.dto.CourseEnrollDTO;
import com.java.school.online_video_training.dto.CourseResponseDTO;
import com.java.school.online_video_training.dto.PageDTO;
import com.java.school.online_video_training.mapper.CourseMapper;
import com.java.school.online_video_training.service.CourseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseController {
	private final CourseService courseService;
	private final CourseMapper courseMapper;

	@PreAuthorize("hasAuthority('course:write')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CourseDTO courseDTO) {
		  System.out.println("DTO: " + courseDTO);
		  System.out.println("CategoryId: " + courseDTO.getCategoryId());
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

    @PreAuthorize("hasAuthority('course:read')")
    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getCourseDetail(@PathVariable Long id){
        CourseDetailDTO courseDetail = courseService.getCourseDetail(id);
        return ResponseEntity.ok(courseDetail);
    }
	
	@PreAuthorize("hasAuthority('course:read')")
	@PostMapping("/enroll")
	public ResponseEntity<?> enroll(@RequestBody CourseEnrollDTO enrollDTO) {
	    courseService.enroll(enrollDTO.getCourseId(), enrollDTO.getUserId());
	    return ResponseEntity.ok("Enrollment request submitted to the author.");
	}
}
