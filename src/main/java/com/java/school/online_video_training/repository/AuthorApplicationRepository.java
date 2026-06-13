package com.java.school.online_video_training.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.java.school.online_video_training.config.security.AuthorApprovalStatus;
import com.java.school.online_video_training.entity.AuthorApplication;
import com.java.school.online_video_training.entity.User;

public interface AuthorApplicationRepository
		extends JpaRepository<AuthorApplication, Long>, JpaSpecificationExecutor<AuthorApplication> {
//	Optional<AuthorApplication> findByActionToken(String actionToken);

	boolean existsByApplicantAndStatus(User applicant, AuthorApprovalStatus status);

	List<AuthorApplication> findAllByOrderByRequestedAtDesc();

	Optional<AuthorApplication> findTopByApplicantOrderByRequestedAtDesc(User applicant);
	
	Page<AuthorApplication> findAll(Pageable pageable);

//	Optional<AuthorApplication> findByActionTokenAndStatus(String token, AuthorApprovalStatus status);
}
