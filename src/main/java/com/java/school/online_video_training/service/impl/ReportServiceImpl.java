package com.java.school.online_video_training.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.java.school.online_video_training.dto.ChartPointDTO;
import com.java.school.online_video_training.dto.ReportDashboardResponse;
import com.java.school.online_video_training.dto.ReportSummaryDTO;
import com.java.school.online_video_training.entity.User;
import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.projection.VideoReportProjection;
import com.java.school.online_video_training.repository.CourseRepository;
import com.java.school.online_video_training.repository.UserRepository;
import com.java.school.online_video_training.repository.VideoRepository;
import com.java.school.online_video_training.service.ReportService;

import lombok.RequiredArgsConstructor;

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
	
	@Override
	public ReportDashboardResponse getReportDashboard(String period, LocalDateTime start, LocalDateTime end) {
		LocalDateTime[] range = resolveRange(period, start, end);
		LocalDateTime startDate = range[0];
		LocalDateTime endDate = range[1];

		List<UserReportProjection> usersInRange = userRepository.getUsersBetween(startDate, endDate);
		List<User> allUsers = userRepository.findAll();
		long totalVideos = videoRepository.count();

		Map<LocalDate, Long> trendMap = usersInRange.stream()
				.collect(Collectors.groupingBy(u -> u.getJoinDate().toLocalDate(), TreeMap::new, Collectors.counting()));

		List<ChartPointDTO> userTrend = trendMap.entrySet().stream()
				.map(entry -> new ChartPointDTO(entry.getKey().format(DateTimeFormatter.ISO_DATE), entry.getValue()))
				.collect(Collectors.toList());

		Map<String, Long> roleMap = new TreeMap<>();
		for (User user : allUsers) {
			if (user.getJoinDate() == null || user.getJoinDate().isBefore(startDate) || user.getJoinDate().isAfter(endDate)) {
				continue;
			}
			if (user.getRoles() == null || user.getRoles().isEmpty()) {
				roleMap.merge("NO_ROLE", 1L, Long::sum);
				continue;
			}
			user.getRoles().forEach(role -> roleMap.merge(role.getName(), 1L, Long::sum));
		}

		List<ChartPointDTO> roleBreakdown = roleMap.entrySet().stream()
				.map(entry -> new ChartPointDTO(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(ChartPointDTO::getValue).reversed())
				.collect(Collectors.toList());

		ReportSummaryDTO summary = new ReportSummaryDTO(
				userRepository.count(),
				usersInRange.size(),
				totalVideos
		);

		return new ReportDashboardResponse(
				period == null ? "custom" : period.toLowerCase(),
				startDate.toString(),
				endDate.toString(),
				summary,
				userTrend,
				roleBreakdown
		);
	}

	private LocalDateTime[] resolveRange(String period, LocalDateTime start, LocalDateTime end) {
		LocalDateTime now = LocalDateTime.now();
		if ("daily".equalsIgnoreCase(period)) {
			return new LocalDateTime[] { now.minusDays(1), now };
		}
		if ("weekly".equalsIgnoreCase(period)) {
			return new LocalDateTime[] { now.minusDays(7), now };
		}
		if ("monthly".equalsIgnoreCase(period)) {
			return new LocalDateTime[] { now.minusMonths(1), now };
		}
		if ("yearly".equalsIgnoreCase(period)) {
			return new LocalDateTime[] { now.minusYears(1), now };
		}
		if (start != null && end != null) {
			return new LocalDateTime[] { start, end };
		}
		return new LocalDateTime[] { now.minusDays(7), now };
	}
}
