package com.java.school.online_video_training.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.PaymentRequest;
import com.java.school.online_video_training.entity.Course;
import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public void approve(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus("APPROVED");
        // paymentStatus remains UNPAID
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void reject(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus("REJECTED");
        // paymentStatus remains UNPAID
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void markAsPaid(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        if (!"APPROVED".equalsIgnoreCase(enrollment.getStatus()) || "PAID".equalsIgnoreCase(enrollment.getPaymentStatus())) {
            throw new IllegalStateException("Payment is only allowed for APPROVED and UNPAID enrollments.");
        }
        enrollment.setPaymentStatus("PAID");
        // Set the price from the course entity
        Course course = enrollment.getCourse();
        if (course != null && course.getPrice() != null) {
            enrollment.setPrice(course.getPrice());
        } else {
        	enrollment.setPrice(BigDecimal.ZERO); // Use BigDecimal for money 
        }
        enrollmentRepository.save(enrollment);
    }

    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Enrollment with id " + id + " not found."));
    }

    public boolean isApproved(Long id) {
        Enrollment enrollment = findById(id);
        return "APPROVED".equalsIgnoreCase(enrollment.getStatus());
    }

    public boolean isPaid(Long id) {
        Enrollment enrollment = findById(id);
        return "PAID".equalsIgnoreCase(enrollment.getPaymentStatus());
    }
    
    @Override
    public void payForEnrollment(Long enrollmentId, PaymentRequest request) {
        if (request == null || request.getPrice() == null) {
            throw new IllegalArgumentException("Price must be provided and non-null.");
        }
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        if (!"APPROVED".equalsIgnoreCase(enrollment.getStatus()) || "PAID".equalsIgnoreCase(enrollment.getPaymentStatus())) {
            throw new IllegalStateException("Payment is only allowed for APPROVED and UNPAID enrollments.");
        }
        enrollment.setPaymentStatus("PAID");
        enrollment.setPrice(request.getPrice());
        enrollmentRepository.save(enrollment);
    }
    
    
//    @Override
//    public void markAsPaid(Long enrollmentId) {
//        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
//            .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
//        if (!"APPROVED".equalsIgnoreCase(enrollment.getStatus())) {
//            throw new IllegalStateException("Payment is only allowed for APPROVED enrollments.");
//        }
//        enrollment.setPaymentStatus(PaymentStatus.PAID);
//        enrollmentRepository.save(enrollment);
//    }
    

}