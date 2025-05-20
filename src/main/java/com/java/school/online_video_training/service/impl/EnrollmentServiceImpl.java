package com.java.school.online_video_training.service.impl;

import org.springframework.stereotype.Service;

import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void approve(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus("APPROVED");
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void reject(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus("REJECTED");
        enrollmentRepository.save(enrollment);
    }
}