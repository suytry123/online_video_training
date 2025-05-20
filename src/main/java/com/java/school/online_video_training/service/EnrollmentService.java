package com.java.school.online_video_training.service;

public interface EnrollmentService {
    void approve(Long enrollmentId);
    void reject(Long enrollmentId);
}
