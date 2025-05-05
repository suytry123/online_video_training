package com.java.school.online_video_training.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.service.ReportService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService{

	private final UserRepository userRepository;

	public List<UserReportProjection> getDailyReport() {
		return userRepository.getTodayUsers();
	}

	public List<UserReportProjection> getWeeklyReport() {
		LocalDateTime now = LocalDateTime.now();
		return userRepository.getUsersBetween(now.minusDays(7), now);
	}

	public List<UserReportProjection> getMonthlyReport() {
		LocalDateTime now = LocalDateTime.now();
		return userRepository.getUsersBetween(now.minusMonths(1), now);
	}

	public List<UserReportProjection> getYearlyReport() {
		LocalDateTime now = LocalDateTime.now();
		return userRepository.getUsersBetween(now.minusYears(1), now);
	}

	public List<UserReportProjection> getDropDownReport(LocalDateTime start, LocalDateTime end) {
		return userRepository.getUsersBetween(start, end);
	}

	public List<UserReportProjection> getSpecificReport(LocalDateTime start, LocalDateTime end) {
		return userRepository.getUsersBetween(start, end);
	}
}
