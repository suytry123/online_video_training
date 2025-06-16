package com.java.school.online_video_training.service;

import java.time.LocalDateTime;
import java.util.List;

import com.java.school.online_video_training.projection.UserPaidReportProjection;
import com.java.school.online_video_training.projection.UserReportProjection;
import com.java.school.online_video_training.projection.VideoReportProjection;

public interface ReportService {

	List<UserReportProjection> getDailyReport();

	List<UserReportProjection> getWeeklyReport();

	List<UserReportProjection> getMonthlyReport();

	List<UserReportProjection> getYearlyReport();
	
	List<VideoReportProjection> getDetailedVideoReport();

	List<UserReportProjection> getUserReportBetweenDate(LocalDateTime start, LocalDateTime end);
	
	List<UserPaidReportProjection> getPaidUserReportBetweenDate(LocalDateTime start, LocalDateTime end);
}
