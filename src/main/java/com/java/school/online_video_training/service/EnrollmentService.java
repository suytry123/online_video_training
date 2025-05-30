package com.java.school.online_video_training.service;

import com.java.school.online_video_training.dto.PaymentRequest;
import com.java.school.online_video_training.entity.Enrollment;

public interface EnrollmentService {
    void approve(Long enrollmentId);
    void reject(Long enrollmentId);
    void markAsPaid(Long enrollmentId);
    Enrollment findById(Long id);
    boolean isApproved(Long id);
    boolean isPaid(Long id);
    //void payForEnrollment(Long enrollmentId, BigDecimal amount);
    void payForEnrollment(Long enrollmentId, PaymentRequest request);
}
