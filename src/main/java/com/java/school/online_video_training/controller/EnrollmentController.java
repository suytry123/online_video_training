package com.java.school.online_video_training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.school.online_video_training.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/{enrollmentId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long enrollmentId) {
        enrollmentService.approve(enrollmentId);
        return ResponseEntity.ok("Enrollment approved.");
    }

    @PostMapping("/{enrollmentId}/reject")
    public ResponseEntity<?> reject(@PathVariable Long enrollmentId) {
        enrollmentService.reject(enrollmentId);
        return ResponseEntity.ok("Enrollment rejected.");
    }
}
