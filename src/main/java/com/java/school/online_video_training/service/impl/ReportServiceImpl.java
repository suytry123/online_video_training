package com.java.school.online_video_training.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.projection.VideoReportProjection;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.ReportService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService{

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final VideoRepository videoRepository;

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
	
	@Override
	public List<VideoReportProjection> getDetailedVideoReport() {
		return videoRepository.findAllVideoDetails();
	}

	@Override
	public List<UserReportProjection> getUserReportBetweenDate(LocalDateTime start, LocalDateTime end) {
		return userRepository.getUsersBetween(start, end);
	}
}
