package com.java.school.online_video_training.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.java.school.online_video_training.entity.Enrollment;
import com.java.school.online_video_training.projection.UserPaidReportProjection;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	@Query("SELECT e.user.username AS username, e.user.email AS email, e.price AS paidAmount, e.createdAt AS paidDate "
			+ "FROM Enrollment e WHERE e.paymentStatus = 'PAID' AND e.createdAt >= :start AND e.createdAt <= :end")
	List<UserPaidReportProjection> findPaidUsersBetweenDates(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
}
