package com.java.school.online_video_training.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.entity.Video;
import com.java.school.online_video_training.projection.UserReportProjection;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
	Optional<User> findUserById(Long id);
	Optional<User> findByUsername(String name);
	Optional<User> findByEmail(String email);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	Optional<User> findByApproveToken(String token);
	Optional<User> findByRejectToken(String token);
	@Query("SELECT u.username AS username, u.email AS email, u.joinDate AS joinDate " +
	           "FROM User u WHERE DATE(u.joinDate) = CURRENT_DATE")
	List<UserReportProjection> getTodayUsers();

	@Query("SELECT u.username AS username, u.email AS email, u.joinDate AS joinDate " +
	           "FROM User u WHERE u.joinDate >= :start AND u.joinDate <= :end")
	List<UserReportProjection> getUsersBetween
								(@Param("start") LocalDateTime start,
	                              @Param("end") LocalDateTime end);}
