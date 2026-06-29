package com.java.school.online_video_training.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.enums.EnrollmentStatus;
import com.java.school.online_video_training.enums.PaymentStatus;
import com.java.school.online_video_training.exception.ResourceNotFoundException;
import com.java.school.online_video_training.repository.EnrollmentRepository;
import com.java.school.online_video_training.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
	private final EnrollmentRepository enrollmentRepository;

	@Override
	public void approve(Long enrollmentId) {

		Enrollment enrollment = findById(enrollmentId);

		if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
			throw new IllegalStateException("Only pending enrollments can be approved");
		}

		enrollment.setStatus(EnrollmentStatus.APPROVED);
	}

	@Override
	public void reject(Long enrollmentId) {

		Enrollment enrollment = findById(enrollmentId);

		if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
			throw new IllegalStateException("Only pending enrollments can be rejected");
		}

		enrollment.setStatus(EnrollmentStatus.REJECTED);
	}

	@Override
	public void markAsPaid(Long enrollmentId) {

		Enrollment enrollment = findById(enrollmentId);

		if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
			throw new IllegalStateException("Enrollment must be approved before payment");
		}

		if (enrollment.getPaymentStatus() == PaymentStatus.PAID) {
			throw new IllegalStateException("Enrollment already paid");
		}

		enrollment.setPaymentStatus(PaymentStatus.PAID);

		BigDecimal coursePrice = enrollment.getCourse().getPrice();

		enrollment.setPrice(coursePrice != null ? coursePrice : BigDecimal.ZERO);
	}
	
	@Transactional
	@Override
	public void payForEnrollment(Long enrollmentId) {

	    Enrollment enrollment = findById(enrollmentId);

	    if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
	        throw new IllegalStateException(
	            "Enrollment must be approved before payment."
	        );
	    }

	    if (enrollment.getPaymentStatus() == PaymentStatus.PAID) {
	        throw new IllegalStateException(
	            "Enrollment already paid."
	        );
	    }

	    enrollment.setPaymentStatus(PaymentStatus.PAID);
	    enrollment.setPrice(enrollment.getCourse().getPrice());

	    enrollmentRepository.save(enrollment);
	}

	@Override
	@Transactional(readOnly = true)
	public Enrollment findById(Long id) {

		return enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isApproved(Long enrollmentId) {

		return findById(enrollmentId).getStatus() == EnrollmentStatus.APPROVED;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isPaid(Long enrollmentId) {

		return findById(enrollmentId).getPaymentStatus() == PaymentStatus.PAID;
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