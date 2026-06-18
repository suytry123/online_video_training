package com.java.school.online_video_training.service;

import com.java.school.online_video_training.entity.Enrollment;

public interface EnrollmentService {
	void approve(Long enrollmentId);

    void reject(Long enrollmentId);

    void markAsPaid(Long enrollmentId);

    Enrollment findById(Long id);

    boolean isApproved(Long enrollmentId);

    boolean isPaid(Long enrollmentId);
    
    void payForEnrollment(Long enrollmentId);
}
